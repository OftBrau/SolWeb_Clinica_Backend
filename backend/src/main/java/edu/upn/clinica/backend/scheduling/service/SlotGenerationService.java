package edu.upn.clinica.backend.scheduling.service;

import edu.upn.clinica.backend.constants.ClinicConstants;
import edu.upn.clinica.backend.enums.DayOfWeek;
import edu.upn.clinica.backend.scheduling.dto.AvailableDateDTO;
import edu.upn.clinica.backend.scheduling.dto.TimeSlotDTO;
import edu.upn.clinica.backend.scheduling.model.AvailabilityOverride;
import edu.upn.clinica.backend.scheduling.model.AvailabilityTemplate;
import edu.upn.clinica.backend.scheduling.repository.AvailabilityOverrideRepository;
import edu.upn.clinica.backend.scheduling.repository.AvailabilityTemplateRepository;
import edu.upn.clinica.backend.scheduling.repository.ScheduleAppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SlotGenerationService {

    private final AvailabilityTemplateRepository templateRepository;
    private final AvailabilityOverrideRepository overrideRepository;
    private final ScheduleAppointmentRepository appointmentRepository;

    public SlotGenerationService(AvailabilityTemplateRepository templateRepository,
                                 AvailabilityOverrideRepository overrideRepository,
                                 ScheduleAppointmentRepository appointmentRepository) {
        this.templateRepository = templateRepository;
        this.overrideRepository = overrideRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<TimeSlotDTO> getAvailableSlots(Integer doctorId, LocalDate date) {
        String dayOfWeek = DayOfWeek.from(date.getDayOfWeek()).name();

        List<AvailabilityTemplate> templates = templateRepository.findByDoctorIdAndDayOfWeekOrderByStartTime(doctorId, dayOfWeek);
        List<AvailabilityOverride> overrides = overrideRepository.findByDoctorIdAndDateOrderByStartTime(doctorId, date);

        List<TimeRange> ranges = new ArrayList<>();
        for (AvailabilityTemplate t : templates) {
            ranges.add(new TimeRange(t.getStartTime(), t.getEndTime()));
        }

        for (AvailabilityOverride o : overrides) {
            TimeRange or = new TimeRange(o.getStartTime(), o.getEndTime());
            if ("BLOCK".equals(o.getOverrideType())) {
                ranges = subtractRange(ranges, or);
            } else if ("ADD".equals(o.getOverrideType())) {
                ranges = addRange(ranges, or);
            }
        }

        Set<LocalTime> bookedTimes = appointmentRepository.findByDoctorAndDate(doctorId, date)
                .stream()
                .map(a -> a.getStartTime())
                .collect(Collectors.toSet());

        List<TimeSlotDTO> slots = new ArrayList<>();
        for (TimeRange range : ranges) {
            LocalTime slotStart = range.start;
            while (!slotStart.isAfter(range.end.minusMinutes(ClinicConstants.SLOT_DURATION_MINUTES))) {
                TimeSlotDTO slot = new TimeSlotDTO(slotStart.toString(), slotStart.plusMinutes(ClinicConstants.SLOT_DURATION_MINUTES).toString());
                if (!bookedTimes.contains(slotStart)) {
                    slots.add(slot);
                }
                slotStart = slotStart.plusMinutes(ClinicConstants.SLOT_DURATION_MINUTES);
            }
        }

        return slots;
    }

    public List<AvailableDateDTO> getAvailableDates(Integer doctorId, YearMonth month) {
        List<AvailableDateDTO> result = new ArrayList<>();
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (date.isBefore(LocalDate.now(ClinicConstants.TIMEZONE))) continue;
            if (date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) continue;
            int count = getAvailableSlots(doctorId, date).size();
            if (count > 0) {
                result.add(new AvailableDateDTO(date.toString(), count));
            }
        }
        return result;
    }

    private List<TimeRange> subtractRange(List<TimeRange> ranges, TimeRange toRemove) {
        List<TimeRange> result = new ArrayList<>();
        for (TimeRange r : ranges) {
            if (r.end.isBefore(toRemove.start) || r.start.isAfter(toRemove.end)) {
                result.add(r);
            } else {
                if (r.start.isBefore(toRemove.start)) {
                    result.add(new TimeRange(r.start, toRemove.start));
                }
                if (r.end.isAfter(toRemove.end)) {
                    result.add(new TimeRange(toRemove.end, r.end));
                }
            }
        }
        return result;
    }

    private List<TimeRange> addRange(List<TimeRange> ranges, TimeRange toAdd) {
        List<TimeRange> result = new ArrayList<>(ranges);
        result.add(toAdd);
        return mergeRanges(result);
    }

    private List<TimeRange> mergeRanges(List<TimeRange> ranges) {
        if (ranges.isEmpty()) return ranges;
        ranges.sort(Comparator.comparing(r -> r.start));
        List<TimeRange> merged = new ArrayList<>();
        TimeRange current = ranges.get(0);
        for (int i = 1; i < ranges.size(); i++) {
            TimeRange next = ranges.get(i);
            if (!current.end.isBefore(next.start)) {
                current = new TimeRange(current.start, max(current.end, next.end));
            } else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        return merged;
    }

    private LocalTime max(LocalTime a, LocalTime b) {
        return a.isAfter(b) ? a : b;
    }

    private static class TimeRange {
        final LocalTime start;
        final LocalTime end;
        TimeRange(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }
    }
}

package edu.upn.clinica.backend.scheduling.service;

import edu.upn.clinica.backend.constants.ClinicConstants;
import edu.upn.clinica.backend.scheduling.dto.AvailabilityOverrideDTO;
import edu.upn.clinica.backend.scheduling.dto.AvailabilityTemplateDTO;
import edu.upn.clinica.backend.scheduling.dto.CreateOverrideRequest;
import edu.upn.clinica.backend.scheduling.model.AvailabilityOverride;
import edu.upn.clinica.backend.scheduling.model.AvailabilityTemplate;
import edu.upn.clinica.backend.scheduling.repository.AvailabilityOverrideRepository;
import edu.upn.clinica.backend.scheduling.repository.AvailabilityTemplateRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class DoctorAvailabilityService {

    private final AvailabilityTemplateRepository templateRepository;
    private final AvailabilityOverrideRepository overrideRepository;

    public DoctorAvailabilityService(AvailabilityTemplateRepository templateRepository,
                                     AvailabilityOverrideRepository overrideRepository) {
        this.templateRepository = templateRepository;
        this.overrideRepository = overrideRepository;
    }

    public List<AvailabilityTemplateDTO> getTemplate(Integer doctorId) {
        return templateRepository.findByDoctorIdOrderByDayOfWeekAscStartTimeAsc(doctorId).stream()
                .map(AvailabilityTemplateDTO::new)
                .toList();
    }

    @Transactional
    public List<AvailabilityTemplateDTO> replaceTemplate(Integer doctorId, List<AvailabilityTemplateDTO> dtos) {
        for (AvailabilityTemplateDTO dto : dtos) {
            validateTemplateRange(dto.getStartTime(), dto.getEndTime());
        }
        templateRepository.deleteByDoctorId(doctorId);
        for (AvailabilityTemplateDTO dto : dtos) {
            AvailabilityTemplate t = new AvailabilityTemplate();
            t.setDoctorId(doctorId);
            t.setDayOfWeek(dto.getDayOfWeek());
            t.setStartTime(LocalTime.parse(dto.getStartTime()));
            t.setEndTime(LocalTime.parse(dto.getEndTime()));
            templateRepository.save(t);
        }
        return getTemplate(doctorId);
    }

    public List<AvailabilityOverrideDTO> getOverrides(Integer doctorId, YearMonth month) {
        return overrideRepository.findByDoctorAndMonth(doctorId, month.getYear(), month.getMonthValue())
                .stream()
                .map(AvailabilityOverrideDTO::new)
                .toList();
    }

    public AvailabilityOverrideDTO createOverride(Integer doctorId, CreateOverrideRequest request) {
        LocalDate date = LocalDate.parse(request.getDate());
        if (date.isBefore(LocalDate.now(ClinicConstants.TIMEZONE))) {
            throw new AppException("No se pueden crear excepciones en fechas pasadas", HttpStatus.BAD_REQUEST);
        }
        validateTemplateRange(request.getStartTime(), request.getEndTime());

        AvailabilityOverride o = new AvailabilityOverride();
        o.setDoctorId(doctorId);
        o.setDate(date);
        o.setStartTime(LocalTime.parse(request.getStartTime()));
        o.setEndTime(LocalTime.parse(request.getEndTime()));
        o.setOverrideType(request.getOverrideType());
        o.setReason(request.getReason());
        o = overrideRepository.save(o);
        return new AvailabilityOverrideDTO(o);
    }

    public void deleteOverride(Integer overrideId) {
        overrideRepository.findById(overrideId)
                .orElseThrow(() -> new AppException("Excepción no encontrada", HttpStatus.NOT_FOUND));
        overrideRepository.deleteById(overrideId);
    }

    private void validateTemplateRange(String start, String end) {
        LocalTime startTime = LocalTime.parse(start);
        LocalTime endTime = LocalTime.parse(end);
        if (startTime.isBefore(ClinicConstants.CLINIC_DAY_START) || endTime.isAfter(ClinicConstants.CLINIC_DAY_END)) {
            throw new AppException("El horario debe estar entre " + ClinicConstants.CLINIC_DAY_START + " y " + ClinicConstants.CLINIC_DAY_END,
                    HttpStatus.BAD_REQUEST);
        }
        if (endTime.minusMinutes(ClinicConstants.SLOT_DURATION_MINUTES).isBefore(startTime)) {
            throw new AppException("La duración mínima es de " + ClinicConstants.SLOT_DURATION_MINUTES + " minutos",
                    HttpStatus.BAD_REQUEST);
        }
    }
}

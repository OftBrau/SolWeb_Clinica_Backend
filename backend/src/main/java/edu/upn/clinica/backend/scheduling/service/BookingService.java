package edu.upn.clinica.backend.scheduling.service;

import edu.upn.clinica.backend.constants.ClinicConstants;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.scheduling.dto.*;
import edu.upn.clinica.backend.scheduling.model.ScheduleAppointment;
import edu.upn.clinica.backend.scheduling.repository.ScheduleAppointmentRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final SlotGenerationService slotGenerationService;
    private final ScheduleAppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    public BookingService(SlotGenerationService slotGenerationService,
                          ScheduleAppointmentRepository appointmentRepository,
                          DoctorRepository doctorRepository) {
        this.slotGenerationService = slotGenerationService;
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }

    public List<DoctorSummaryDTO> listDoctors(String specialty) {
        return doctorRepository.findAll().stream()
                .filter(d -> specialty == null || specialty.isBlank() || d.getEspecialidad().equalsIgnoreCase(specialty))
                .map(d -> {
                    String fullName = d.getNombre();
                    String firstName = fullName.contains(" ") ? fullName.substring(0, fullName.indexOf(' ')) : fullName;
                    String lastName = fullName.contains(" ") ? fullName.substring(fullName.indexOf(' ') + 1) : "";
                    return new DoctorSummaryDTO(d.getIdDoctor(), firstName, lastName, d.getEspecialidad());
                })
                .collect(Collectors.toList());
    }

    public List<AvailableDateDTO> getAvailableDates(Integer doctorId, YearMonth month) {
        return slotGenerationService.getAvailableDates(doctorId, month);
    }

    public List<TimeSlotDTO> getAvailableSlots(Integer doctorId, LocalDate date) {
        return slotGenerationService.getAvailableSlots(doctorId, date);
    }

    @Transactional
    public AppointmentDTO bookAppointment(Integer patientId, BookAppointmentRequest request) {
        LocalDate date = LocalDate.parse(request.getDate());
        LocalTime startTime = LocalTime.parse(request.getStartTime());
        LocalTime endTime = startTime.plusMinutes(ClinicConstants.SLOT_DURATION_MINUTES);

        if (date.isBefore(LocalDate.now(ClinicConstants.TIMEZONE))) {
            throw new AppException("No se pueden agendar citas en fechas pasadas", HttpStatus.BAD_REQUEST);
        }

        List<TimeSlotDTO> available = getAvailableSlots(request.getDoctorId(), date);
        boolean slotAvailable = available.stream()
                .anyMatch(s -> s.getStartTime().equals(request.getStartTime()));
        if (!slotAvailable) {
            throw new AppException("SLOT_NOT_AVAILABLE", "Este horario no está disponible. Por favor selecciona otro.",
                    HttpStatus.CONFLICT);
        }

        if (appointmentRepository.existsByDoctorIdAndDateAndStartTimeAndStatus(request.getDoctorId(), date, startTime, "SCHEDULED")) {
            throw new AppException("SLOT_ALREADY_BOOKED",
                    "Este horario acaba de ser reservado por otro paciente. Por favor selecciona otro.",
                    HttpStatus.CONFLICT);
        }

        if (appointmentRepository.existsByPatientIdAndDoctorIdAndDateAndStatus(patientId, request.getDoctorId(), date, "SCHEDULED")) {
            throw new AppException("Ya tienes una cita agendada con este doctor el mismo día",
                    HttpStatus.BAD_REQUEST);
        }

        ScheduleAppointment a = new ScheduleAppointment();
        a.setPatientId(patientId);
        a.setDoctorId(request.getDoctorId());
        a.setDate(date);
        a.setStartTime(startTime);
        a.setEndTime(endTime);
        a.setStatus("SCHEDULED");
        a = appointmentRepository.save(a);

        AppointmentDTO dto = new AppointmentDTO(a);
                    doctorRepository.findById(request.getDoctorId()).ifPresent(d ->
                dto.setDoctorName(d.getNombre()));
        return dto;
    }

    public List<AppointmentDTO> getPatientAppointments(Integer patientId, String status) {
        List<ScheduleAppointment> appointments;
        if (status != null && !status.isBlank()) {
            appointments = appointmentRepository.findByPatientIdAndStatusOrderByDateDescStartTimeDesc(patientId, status);
        } else {
            appointments = appointmentRepository.findByPatientIdOrderByDateDescStartTimeDesc(patientId);
        }
        return appointments.stream().map(a -> {
            AppointmentDTO dto = new AppointmentDTO(a);
            if (a.getDoctorId() != null) {
                doctorRepository.findById(a.getDoctorId()).ifPresent(d ->
                        dto.setDoctorName(d.getNombre()));
            }
            return dto;
        }).toList();
    }

    @Transactional
    public AppointmentDTO cancelAppointment(Integer patientId, Integer appointmentId) {
        ScheduleAppointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException("APPOINTMENT_NOT_FOUND", "Cita no encontrada",
                        HttpStatus.NOT_FOUND));

        if (!a.getPatientId().equals(patientId)) {
            throw new AppException("UNAUTHORIZED", "No puedes cancelar una cita que no te pertenece",
                    HttpStatus.FORBIDDEN);
        }

        if (!"SCHEDULED".equals(a.getStatus()) && !"CONFIRMADA".equals(a.getStatus())
                && !"PENDIENTE_ASIGNACION".equals(a.getStatus())) {
            throw new AppException("Solo puedes cancelar citas activas", HttpStatus.BAD_REQUEST);
        }

        if (a.getDate().isBefore(LocalDate.now(ClinicConstants.TIMEZONE))) {
            throw new AppException("Solo puedes cancelar citas de fechas futuras",
                    HttpStatus.BAD_REQUEST);
        }

        appointmentRepository.updateStatus(appointmentId, "CANCELLED");
        a.setStatus("CANCELLED");
        AppointmentDTO dto = new AppointmentDTO(a);
        doctorRepository.findById(a.getDoctorId()).ifPresent(d ->
                dto.setDoctorName(d.getNombre()));
        return dto;
    }

    public List<AppointmentDTO> getDoctorAppointments(Integer doctorId, LocalDate from, LocalDate to) {
        return appointmentRepository.findByDoctorIdAndDateBetweenOrderByDateAscStartTimeAsc(doctorId, from, to).stream().map(a -> {
            AppointmentDTO dto = new AppointmentDTO(a);
            if (a.getDoctorId() != null) {
                doctorRepository.findById(a.getDoctorId()).ifPresent(d ->
                        dto.setDoctorName(d.getNombre()));
            }
            return dto;
        }).toList();
    }

    @Transactional
    public AppointmentDTO updateAppointmentStatus(Integer doctorId, Integer appointmentId, String status) {
        ScheduleAppointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException("APPOINTMENT_NOT_FOUND", "Cita no encontrada",
                        HttpStatus.NOT_FOUND));

        if (!a.getDoctorId().equals(doctorId)) {
            throw new AppException("UNAUTHORIZED", "No puedes modificar una cita que no te pertenece",
                    HttpStatus.FORBIDDEN);
        }

        if ("COMPLETED".equals(status) && a.getDate().isAfter(LocalDate.now(ClinicConstants.TIMEZONE))) {
            throw new AppException("No puedes marcar como completada una cita futura", HttpStatus.BAD_REQUEST);
        }

        appointmentRepository.updateStatus(appointmentId, status);
        a.setStatus(status);
        AppointmentDTO dto = new AppointmentDTO(a);
        doctorRepository.findById(a.getDoctorId()).ifPresent(d ->
                dto.setDoctorName(d.getNombre()));
        return dto;
    }
}

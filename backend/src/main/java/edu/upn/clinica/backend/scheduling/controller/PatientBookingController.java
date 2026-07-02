package edu.upn.clinica.backend.scheduling.controller;

import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.scheduling.dto.*;
import edu.upn.clinica.backend.scheduling.service.BookingService;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PatientBookingController {

    private final BookingService bookingService;
    private final DoctorRepository doctorRepository;
    private final PacienteRepository pacienteRepository;

    public PatientBookingController(BookingService bookingService,
                                    DoctorRepository doctorRepository,
                                    PacienteRepository pacienteRepository) {
        this.bookingService = bookingService;
        this.doctorRepository = doctorRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorSummaryDTO>> listDoctors(
            @RequestParam(required = false) String specialty) {
        return ResponseEntity.ok(bookingService.listDoctors(specialty));
    }

    @GetMapping("/doctors/{id}/available-dates")
    public ResponseEntity<List<AvailableDateDTO>> getAvailableDates(
            @PathVariable Integer id,
            @RequestParam(required = false) String month) {
        YearMonth ym = month != null ? YearMonth.parse(month) : YearMonth.now();
        return ResponseEntity.ok(bookingService.getAvailableDates(id, ym));
    }

    @GetMapping("/doctors/{id}/available-slots")
    public ResponseEntity<List<TimeSlotDTO>> getAvailableSlots(
            @PathVariable Integer id,
            @RequestParam String date) {
        LocalDate d = LocalDate.parse(date);
        return ResponseEntity.ok(bookingService.getAvailableSlots(id, d));
    }

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentDTO> bookAppointment(
            Authentication auth,
            @RequestBody BookAppointmentRequest request) {
        Integer patientId = getPatientId(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.bookAppointment(patientId, request));
    }

    @GetMapping("/patients/me/appointments")
    public ResponseEntity<List<AppointmentDTO>> myAppointments(
            Authentication auth,
            @RequestParam(required = false) String status) {
        Integer patientId = getPatientId(auth);
        return ResponseEntity.ok(bookingService.getPatientAppointments(patientId, status));
    }

    @PatchMapping("/patients/me/appointments/{id}/cancel")
    public ResponseEntity<AppointmentDTO> cancelAppointment(
            Authentication auth,
            @PathVariable Integer id) {
        Integer patientId = getPatientId(auth);
        return ResponseEntity.ok(bookingService.cancelAppointment(patientId, id));
    }

    private Integer getPatientId(Authentication auth) {
        String email = auth.getName();
        return pacienteRepository.findByEmail(email)
                .map(p -> p.getIdPaciente())
                .orElseThrow(() -> new AppException("Paciente no encontrado", HttpStatus.NOT_FOUND));
    }
}

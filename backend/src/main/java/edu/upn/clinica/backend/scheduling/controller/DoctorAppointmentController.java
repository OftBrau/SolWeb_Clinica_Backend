package edu.upn.clinica.backend.scheduling.controller;

import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.scheduling.dto.AppointmentDTO;
import edu.upn.clinica.backend.scheduling.dto.UpdateStatusRequest;
import edu.upn.clinica.backend.scheduling.service.BookingService;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors/me/appointments")
public class DoctorAppointmentController {

    private final BookingService bookingService;
    private final DoctorRepository doctorRepository;

    public DoctorAppointmentController(BookingService bookingService,
                                       DoctorRepository doctorRepository) {
        this.bookingService = bookingService;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAppointments(
            Authentication auth,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        Integer doctorId = getDoctorId(auth);
        LocalDate fromDate = from != null ? LocalDate.parse(from) : LocalDate.now().minusDays(7);
        LocalDate toDate = to != null ? LocalDate.parse(to) : LocalDate.now().plusDays(30);
        return ResponseEntity.ok(bookingService.getDoctorAppointments(doctorId, fromDate, toDate));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AppointmentDTO> updateStatus(
            Authentication auth,
            @PathVariable Integer id,
            @RequestBody UpdateStatusRequest request) {
        Integer doctorId = getDoctorId(auth);
        return ResponseEntity.ok(bookingService.updateAppointmentStatus(doctorId, id, request.getStatus()));
    }

    private Integer getDoctorId(Authentication auth) {
        String email = auth.getName();
        return doctorRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException("Doctor no encontrado", HttpStatus.NOT_FOUND));
    }
}

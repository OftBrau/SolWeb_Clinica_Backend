package edu.upn.clinica.backend.scheduling.controller;

import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.scheduling.dto.AvailabilityOverrideDTO;
import edu.upn.clinica.backend.scheduling.dto.AvailabilityTemplateDTO;
import edu.upn.clinica.backend.scheduling.dto.CreateOverrideRequest;
import edu.upn.clinica.backend.scheduling.service.DoctorAvailabilityService;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors/me/availability")
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService availabilityService;
    private final DoctorRepository doctorRepository;

    public DoctorAvailabilityController(DoctorAvailabilityService availabilityService,
                                        DoctorRepository doctorRepository) {
        this.availabilityService = availabilityService;
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/template")
    public ResponseEntity<List<AvailabilityTemplateDTO>> getTemplate(Authentication auth) {
        Integer doctorId = getDoctorId(auth);
        return ResponseEntity.ok(availabilityService.getTemplate(doctorId));
    }

    @PutMapping("/template")
    public ResponseEntity<List<AvailabilityTemplateDTO>> replaceTemplate(
            Authentication auth, @RequestBody List<AvailabilityTemplateDTO> templates) {
        Integer doctorId = getDoctorId(auth);
        return ResponseEntity.ok(availabilityService.replaceTemplate(doctorId, templates));
    }

    @GetMapping("/overrides")
    public ResponseEntity<List<AvailabilityOverrideDTO>> getOverrides(
            Authentication auth, @RequestParam(required = false) String month) {
        Integer doctorId = getDoctorId(auth);
        YearMonth ym = month != null ? YearMonth.parse(month) : YearMonth.now();
        return ResponseEntity.ok(availabilityService.getOverrides(doctorId, ym));
    }

    @PostMapping("/overrides")
    public ResponseEntity<AvailabilityOverrideDTO> createOverride(
            Authentication auth, @RequestBody CreateOverrideRequest request) {
        Integer doctorId = getDoctorId(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(availabilityService.createOverride(doctorId, request));
    }

    @DeleteMapping("/overrides/{id}")
    public ResponseEntity<Void> deleteOverride(Authentication auth, @PathVariable Integer id) {
        Integer doctorId = getDoctorId(auth);
        availabilityService.deleteOverride(id);
        return ResponseEntity.noContent().build();
    }

    private Integer getDoctorId(Authentication auth) {
        String email = auth.getName();
        return doctorRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException("Doctor no encontrado", HttpStatus.NOT_FOUND));
    }
}

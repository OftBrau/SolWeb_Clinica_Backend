package edu.upn.clinica.backend.teleconsulta.nota.controller;

import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.teleconsulta.nota.dto.CrearNotaRequest;
import edu.upn.clinica.backend.teleconsulta.nota.dto.NotaTeleconsultaDTO;
import edu.upn.clinica.backend.teleconsulta.nota.service.NotaTeleconsultaService;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teleconsulta/{idTeleconsulta}/notas")
@Tag(name = "Notas Teleconsulta", description = "Notas medicas durante teleconsulta (CUS_19)")
public class NotaTeleconsultaController {

    @Autowired private NotaTeleconsultaService notaService;
    @Autowired private DoctorRepository doctorRepository;

    @PostMapping
    @Operation(summary = "Registrar nota en teleconsulta (doctor)")
    public ResponseEntity<ApiResponse<NotaTeleconsultaDTO>> crear(
            @PathVariable Integer idTeleconsulta,
            @Valid @RequestBody CrearNotaRequest req) {
        Integer idDoctor = getIdDoctor();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Nota registrada", notaService.crear(idTeleconsulta, idDoctor, req)));
    }

    @GetMapping
    @Operation(summary = "Listar notas de una teleconsulta")
    public ResponseEntity<ApiResponse<List<NotaTeleconsultaDTO>>> listar(
            @PathVariable Integer idTeleconsulta) {
        return ResponseEntity.ok(ApiResponse.ok("Notas obtenidas", notaService.listar(idTeleconsulta)));
    }

    private Integer getIdDoctor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return doctorRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException("Doctor no encontrado", HttpStatus.NOT_FOUND));
    }
}

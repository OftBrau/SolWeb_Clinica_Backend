package edu.upn.clinica.backend.cita.controller;

import edu.upn.clinica.backend.cita.dto.CitaPublicaResponse;
import edu.upn.clinica.backend.cita.service.CitaPacienteService;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/citas")
@Tag(name = "Mis Citas", description = "Gestión de citas del paciente autenticado")
public class CitaPacienteController {

    @Autowired private CitaPacienteService citaPacienteService;
    @Autowired private PacienteRepository  pacienteRepository;

    // GET /api/citas/mis-citas
    @GetMapping("/mis-citas")
    @Operation(summary = "Listar mis citas")
    public ResponseEntity<ApiResponse<List<CitaPublicaResponse>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok("Citas",
                citaPacienteService.listarMisCitas(getIdPaciente())));
    }

    // PUT /api/citas/{id}/cancelar
    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una cita")
    public ResponseEntity<ApiResponse<Void>> cancelar(@PathVariable Integer id) {
        citaPacienteService.cancelar(id, getIdPaciente());
        return ResponseEntity.ok(ApiResponse.ok("Cita cancelada", null));
    }

    // PUT /api/citas/{id}/reprogramar
    @PutMapping("/{id}/reprogramar")
    @Operation(summary = "Reprogramar una cita")
    public ResponseEntity<ApiResponse<CitaPublicaResponse>> reprogramar(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        CitaPublicaResponse actualizada = citaPacienteService.reprogramar(
                id, getIdPaciente(), body.get("fecha"), body.get("hora"));
        return ResponseEntity.ok(ApiResponse.ok("Cita reprogramada", actualizada));
    }

    // Helper: obtiene idPaciente desde el email del JWT
    private Integer getIdPaciente() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return pacienteRepository
                .findByEmail(email)
                .map(p -> p.getIdPaciente())
                .orElseThrow(() -> new AppException(
                        "Paciente no encontrado", HttpStatus.NOT_FOUND));
    }
}
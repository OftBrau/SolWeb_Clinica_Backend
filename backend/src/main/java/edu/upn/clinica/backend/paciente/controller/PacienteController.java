package edu.upn.clinica.backend.paciente.controller;

import edu.upn.clinica.backend.paciente.dto.PacienteDTO;
import edu.upn.clinica.backend.paciente.service.PacienteService;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.shared.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes", description = "Gestión de pacientes")
@SecurityRequirement(name = "bearerAuth")
public class PacienteController {

    @Autowired private PacienteService    pacienteService;
    @Autowired private PacienteRepository pacienteRepository;

    // GET /api/pacientes?page=0&size=10
    @GetMapping
    @Operation(summary = "Listar pacientes con paginación")
    public ResponseEntity<ApiResponse<PageResult<PacienteDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok("Pacientes obtenidos",
                pacienteService.listar(page, size)));
    }

    // GET /api/pacientes/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Buscar paciente por ID")
    public ResponseEntity<ApiResponse<PacienteDTO>> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Paciente encontrado",
                pacienteService.buscarPorId(id)));
    }

    // GET /api/pacientes/email/{email}  ← NUEVO
    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar paciente por email")
    public ResponseEntity<ApiResponse<PacienteDTO>> buscarPorEmail(@PathVariable String email) {
        Integer id = pacienteRepository.findByEmail(email)
                .map(p -> p.getIdPaciente())
                .orElseThrow(() -> new AppException(
                        "Paciente no encontrado", HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(ApiResponse.ok("Paciente encontrado",
                pacienteService.buscarPorId(id)));
    }

    // POST /api/pacientes
    @PostMapping
    @Operation(summary = "Registrar nuevo paciente")
    public ResponseEntity<ApiResponse<PacienteDTO>> registrar(
            @Valid @RequestBody PacienteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Paciente registrado exitosamente",
                        pacienteService.registrar(dto)));
    }

    // PUT /api/pacientes/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos del paciente")
    public ResponseEntity<ApiResponse<PacienteDTO>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody PacienteDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Paciente actualizado",
                pacienteService.actualizar(id, dto)));
    }

    // DELETE /api/pacientes/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar paciente (soft delete)")
    public ResponseEntity<ApiResponse<?>> desactivar(@PathVariable Integer id) {
        pacienteService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Paciente desactivado"));
    }
}
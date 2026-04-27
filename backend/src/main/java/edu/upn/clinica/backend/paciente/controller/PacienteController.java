package edu.upn.clinica.backend.paciente.controller;

import edu.upn.clinica.backend.paciente.dto.PacienteDTO;
import edu.upn.clinica.backend.paciente.service.PacienteService;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ============================================================
//  PacienteController.java
//  Endpoints REST del módulo paciente
//  Base: /api/pacientes
// ============================================================
@RestController
@RequestMapping("/api/pacientes")
@Tag(name = "Pacientes", description = "Gestión de pacientes")
@SecurityRequirement(name = "bearerAuth")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    // GET /api/pacientes?page=0&size=10
    @GetMapping
    @Operation(summary = "Listar pacientes con paginación")
    public ResponseEntity<ApiResponse<PageResult<PacienteDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResult<PacienteDTO> resultado = pacienteService.listar(page, size);
        return ResponseEntity.ok(ApiResponse.ok("Pacientes obtenidos", resultado));
    }

    // GET /api/pacientes/{id}
    @GetMapping("/{id}")
    @Operation(summary = "Buscar paciente por ID")
    public ResponseEntity<ApiResponse<PacienteDTO>> buscarPorId(@PathVariable Integer id) {
        PacienteDTO paciente = pacienteService.buscarPorId(id);
        return ResponseEntity.ok(ApiResponse.ok("Paciente encontrado", paciente));
    }

    // POST /api/pacientes
    @PostMapping
    @Operation(summary = "Registrar nuevo paciente")
    public ResponseEntity<ApiResponse<PacienteDTO>> registrar(
            @Valid @RequestBody PacienteDTO dto) {

        PacienteDTO nuevo = pacienteService.registrar(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Paciente registrado exitosamente", nuevo));
    }

    // PUT /api/pacientes/{id}
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos del paciente")
    public ResponseEntity<ApiResponse<PacienteDTO>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody PacienteDTO dto) {

        PacienteDTO actualizado = pacienteService.actualizar(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Paciente actualizado", actualizado));
    }

    // DELETE /api/pacientes/{id}
    @DeleteMapping("/{id}")
    @Operation(summary = "Desactivar paciente (soft delete)")
    public ResponseEntity<ApiResponse<?>> desactivar(@PathVariable Integer id) {
        pacienteService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Paciente desactivado"));
    }
}
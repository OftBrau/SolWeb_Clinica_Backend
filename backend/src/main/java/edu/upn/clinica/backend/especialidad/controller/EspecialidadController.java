package edu.upn.clinica.backend.especialidad.controller;

import edu.upn.clinica.backend.especialidad.dto.EspecialidadDTO;
import edu.upn.clinica.backend.especialidad.service.EspecialidadService;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/especialidades")
@Tag(name = "Especialidades", description = "Configuración de especialidades médicas (CUS_45)")
@SecurityRequirement(name = "bearerAuth")
public class EspecialidadController {

    @Autowired
    private EspecialidadService especialidadService;

    @GetMapping
    @Operation(summary = "Listar especialidades")
    public ResponseEntity<ApiResponse<List<EspecialidadDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok("Especialidades obtenidas",
                especialidadService.listar()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener especialidad por ID")
    public ResponseEntity<ApiResponse<EspecialidadDTO>> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Especialidad obtenida",
                especialidadService.obtener(id)));
    }

    @PostMapping
    @Operation(summary = "Crear nueva especialidad")
    public ResponseEntity<ApiResponse<EspecialidadDTO>> crear(
            @Valid @RequestBody EspecialidadDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Especialidad creada correctamente",
                especialidadService.crear(dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar especialidad")
    public ResponseEntity<ApiResponse<EspecialidadDTO>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody EspecialidadDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Especialidad actualizada correctamente",
                especialidadService.actualizar(id, dto)));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar especialidad")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Integer id) {
        especialidadService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Especialidad desactivada correctamente"));
    }
}

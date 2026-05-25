package edu.upn.clinica.backend.horario.controller;

import edu.upn.clinica.backend.horario.dto.HorarioDTO;
import edu.upn.clinica.backend.horario.service.HorarioService;
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
@RequestMapping("/api/admin/horarios")
@Tag(name = "Horarios", description = "Configuración de horarios de atención por especialidad (CUS_46)")
@SecurityRequirement(name = "bearerAuth")
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    @GetMapping
    @Operation(summary = "Listar horarios")
    public ResponseEntity<ApiResponse<List<HorarioDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok("Horarios obtenidos",
                horarioService.listar()));
    }

    @GetMapping("/especialidad/{idEspecialidad}")
    @Operation(summary = "Listar horarios por especialidad")
    public ResponseEntity<ApiResponse<List<HorarioDTO>>> listarPorEspecialidad(
            @PathVariable Integer idEspecialidad) {
        return ResponseEntity.ok(ApiResponse.ok("Horarios obtenidos",
                horarioService.listarPorEspecialidad(idEspecialidad)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener horario por ID")
    public ResponseEntity<ApiResponse<HorarioDTO>> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Horario obtenido",
                horarioService.obtener(id)));
    }

    @PostMapping
    @Operation(summary = "Crear horario de atención")
    public ResponseEntity<ApiResponse<HorarioDTO>> crear(
            @Valid @RequestBody HorarioDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Horario creado correctamente",
                horarioService.crear(dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar horario de atención")
    public ResponseEntity<ApiResponse<HorarioDTO>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody HorarioDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Horario actualizado correctamente",
                horarioService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar horario de atención")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        horarioService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Horario eliminado correctamente"));
    }
}

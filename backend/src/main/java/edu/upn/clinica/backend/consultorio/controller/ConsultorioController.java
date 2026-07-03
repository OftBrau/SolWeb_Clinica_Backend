package edu.upn.clinica.backend.consultorio.controller;

import edu.upn.clinica.backend.consultorio.dto.AsignarConsultorioRequest;
import edu.upn.clinica.backend.consultorio.dto.ConsultorioDTO;
import edu.upn.clinica.backend.consultorio.service.ConsultorioService;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/operaciones/consultorios")
@Tag(name = "Consultorios", description = "Gestión de consultorios y asignación a doctores")
@SecurityRequirement(name = "bearerAuth")
public class ConsultorioController {

    @Autowired
    private ConsultorioService consultorioService;

    @GetMapping
    @Operation(summary = "Listar todos los consultorios")
    public ResponseEntity<ApiResponse<List<ConsultorioDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok("Consultorios obtenidos",
                consultorioService.listar()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener consultorio por ID")
    public ResponseEntity<ApiResponse<ConsultorioDTO>> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Consultorio obtenido",
                consultorioService.obtener(id)));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo consultorio")
    public ResponseEntity<ApiResponse<ConsultorioDTO>> crear(
            @Valid @RequestBody ConsultorioDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Consultorio creado correctamente",
                consultorioService.crear(dto)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar consultorio")
    public ResponseEntity<ApiResponse<ConsultorioDTO>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody ConsultorioDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Consultorio actualizado correctamente",
                consultorioService.actualizar(id, dto)));
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar consultorio")
    public ResponseEntity<ApiResponse<Void>> desactivar(@PathVariable Integer id) {
        consultorioService.desactivar(id);
        return ResponseEntity.ok(ApiResponse.ok("Consultorio desactivado correctamente"));
    }

    @PostMapping("/asignar")
    @Operation(summary = "Asignar consultorio a doctor")
    public ResponseEntity<ApiResponse<Void>> asignar(
            @Valid @RequestBody AsignarConsultorioRequest request) {
        consultorioService.asignarADoctor(request);
        return ResponseEntity.ok(ApiResponse.ok("Consultorio asignado correctamente"));
    }

    @GetMapping("/asignaciones")
    @Operation(summary = "Listar todas las asignaciones doctor-consultorio")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listarAsignaciones() {
        return ResponseEntity.ok(ApiResponse.ok("Asignaciones", consultorioService.listarAsignaciones()));
    }

    @DeleteMapping("/asignaciones/{id}")
    @Operation(summary = "Eliminar una asignacion")
    public ResponseEntity<ApiResponse<Void>> eliminarAsignacion(@PathVariable Integer id) {
        consultorioService.eliminarAsignacion(id);
        return ResponseEntity.ok(ApiResponse.ok("Asignacion eliminada"));
    }

    @PatchMapping("/{id}/activar")
    @Operation(summary = "Reactivar consultorio")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Integer id) {
        consultorioService.activar(id);
        return ResponseEntity.ok(ApiResponse.ok("Consultorio reactivado"));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Consultorios disponibles en un dia y hora")
    public ResponseEntity<ApiResponse<List<ConsultorioDTO>>> disponibles(
            @RequestParam String diaSemana, @RequestParam String hora) {
        return ResponseEntity.ok(ApiResponse.ok("Disponibles", consultorioService.disponibles(diaSemana, hora)));
    }

    @GetMapping("/ocupacion")
    @Operation(summary = "Ocupacion de consultorios en una fecha")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> ocupacion(@RequestParam String fecha) {
        return ResponseEntity.ok(ApiResponse.ok("Ocupacion", consultorioService.ocupacion(fecha)));
    }
}

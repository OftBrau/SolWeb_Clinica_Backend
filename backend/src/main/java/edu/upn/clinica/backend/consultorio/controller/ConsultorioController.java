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
    @Operation(summary = "Asignar consultorio a doctor (CUS_37)")
    public ResponseEntity<ApiResponse<Void>> asignar(
            @Valid @RequestBody AsignarConsultorioRequest request) {
        consultorioService.asignarADoctor(request);
        return ResponseEntity.ok(ApiResponse.ok("Consultorio asignado correctamente"));
    }
}

package edu.upn.clinica.backend.cita.controller;

import edu.upn.clinica.backend.cita.dto.CitaPublicaResponse;
import edu.upn.clinica.backend.cita.service.CitaAdminService;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/operaciones/citas")
@Tag(name = "Gestión de Citas (Admin)", description = "Operaciones administrativas sobre citas")
@SecurityRequirement(name = "bearerAuth")
public class CitaAdminController {

    @Autowired
    private CitaAdminService citaAdminService;

    @GetMapping
    @Operation(summary = "Listar citas por estado (paginado)")
    public ResponseEntity<ApiResponse<PageResult<CitaPublicaResponse>>> listar(
            @RequestParam(defaultValue = "PENDIENTE") String estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok("Citas obtenidas",
                citaAdminService.listarPorEstado(estado, page, size)));
    }

    @PatchMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar cita médica (CUS_34)")
    public ResponseEntity<ApiResponse<CitaPublicaResponse>> confirmar(@PathVariable Integer id) {
        CitaPublicaResponse cita = citaAdminService.confirmar(id);
        return ResponseEntity.ok(ApiResponse.ok("Cita confirmada correctamente", cita));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar cita (gestión administrativa - CUS_35)")
    public ResponseEntity<ApiResponse<Void>> cancelar(@PathVariable Integer id) {
        citaAdminService.cancelar(id);
        return ResponseEntity.ok(ApiResponse.ok("Cita cancelada correctamente"));
    }

    @PatchMapping("/{id}/reprogramar")
    @Operation(summary = "Reprogramar cita (gestión administrativa - CUS_36)")
    public ResponseEntity<ApiResponse<CitaPublicaResponse>> reprogramar(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        CitaPublicaResponse cita = citaAdminService.reprogramar(
                id, body.get("fecha"), body.get("hora"));
        return ResponseEntity.ok(ApiResponse.ok("Cita reprogramada correctamente", cita));
    }
}

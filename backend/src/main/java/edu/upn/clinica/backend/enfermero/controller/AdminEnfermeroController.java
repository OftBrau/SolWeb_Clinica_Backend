package edu.upn.clinica.backend.enfermero.controller;

import edu.upn.clinica.backend.enfermero.model.AsignacionEnfermero;
import edu.upn.clinica.backend.enfermero.service.EnfermeroService;
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
@RequestMapping("/api/admin/enfermeros")
@Tag(name = "Admin Enfermeros", description = "Gestion administrativa de enfermeros")
@SecurityRequirement(name = "bearerAuth")
public class AdminEnfermeroController {

    @Autowired
    private EnfermeroService enfermeroService;

    @GetMapping
    @Operation(summary = "Listar todos los enfermeros con su doctor asignado")
    public ResponseEntity<ApiResponse<List<AsignacionEnfermero>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok("Enfermeros obtenidos",
                enfermeroService.listarEnfermerosDisponibles()));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar enfermeros sin doctor asignado")
    public ResponseEntity<ApiResponse<List<AsignacionEnfermero>>> disponibles() {
        return ResponseEntity.ok(ApiResponse.ok("Enfermeros disponibles",
                enfermeroService.listarEnfermerosDisponibles()));
    }

    @PostMapping("/{id}/asignar")
    @Operation(summary = "Asignar enfermero a un doctor")
    public ResponseEntity<ApiResponse<Void>> asignar(
            @PathVariable Integer id,
            @Valid @RequestBody Map<String, Integer> body) {
        Integer idDoctor = body.get("idDoctor");
        if (idDoctor == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Se requiere idDoctor"));
        }
        enfermeroService.asignarEnfermero(id, idDoctor);
        return ResponseEntity.ok(ApiResponse.ok("Enfermero asignado correctamente"));
    }

    @DeleteMapping("/asignaciones/{id}")
    @Operation(summary = "Desasignar enfermero de su doctor")
    public ResponseEntity<ApiResponse<Void>> desasignar(@PathVariable Integer id) {
        enfermeroService.desasignarEnfermero(id);
        return ResponseEntity.ok(ApiResponse.ok("Enfermero desasignado correctamente"));
    }
}

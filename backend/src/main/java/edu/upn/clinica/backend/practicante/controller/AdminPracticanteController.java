package edu.upn.clinica.backend.practicante.controller;

import edu.upn.clinica.backend.practicante.dto.AsignarPracticanteRequest;
import edu.upn.clinica.backend.practicante.dto.PracticanteDisponibleDTO;
import edu.upn.clinica.backend.practicante.model.PracticanteEntidad;
import edu.upn.clinica.backend.practicante.service.PracticanteService;
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
@RequestMapping("/api/admin/practicantes")
@Tag(name = "Admin Practicantes", description = "Administración de asignación de practicantes a doctores")
@SecurityRequirement(name = "bearerAuth")
public class AdminPracticanteController {

    @Autowired
    private PracticanteService practicanteService;

    @GetMapping
    @Operation(summary = "Listar todas las asignaciones de practicantes")
    public ResponseEntity<ApiResponse<List<PracticanteEntidad>>> listarAsignaciones() {
        return ResponseEntity.ok(ApiResponse.ok("Asignaciones obtenidas",
                practicanteService.listarAsignaciones()));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar practicantes (doctores con rol PRACTICANTE) no asignados aún")
    public ResponseEntity<ApiResponse<List<PracticanteDisponibleDTO>>> listarDisponibles() {
        return ResponseEntity.ok(ApiResponse.ok("Practicantes disponibles obtenidos",
                practicanteService.listarPracticantesDisponibles()));
    }

    @PostMapping("/asignar")
    @Operation(summary = "Asignar un practicante a un doctor supervisor")
    public ResponseEntity<ApiResponse<Void>> asignar(@Valid @RequestBody AsignarPracticanteRequest request) {
        practicanteService.asignarPracticante(request);
        return ResponseEntity.ok(ApiResponse.ok("Practicante asignado correctamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar asignación de practicante")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        practicanteService.eliminarAsignacion(id);
        return ResponseEntity.ok(ApiResponse.ok("Asignación eliminada correctamente"));
    }
}

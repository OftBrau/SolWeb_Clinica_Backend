package edu.upn.clinica.backend.doctor.controller;

import edu.upn.clinica.backend.doctor.dto.DisponibilidadDTO;
import edu.upn.clinica.backend.doctor.service.DoctorService;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctores")
@Tag(name = "Doctores", description = "Gestión de doctores y disponibilidad horaria")
@SecurityRequirement(name = "bearerAuth")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/{idDoctor}/disponibilidad")
    @Operation(summary = "Listar disponibilidad horaria del doctor (CUS_17)")
    public ResponseEntity<ApiResponse<List<DisponibilidadDTO>>> listarDisponibilidad(
            @PathVariable Integer idDoctor) {
        return ResponseEntity.ok(ApiResponse.ok("Disponibilidad obtenida",
                doctorService.listarDisponibilidad(idDoctor)));
    }

    @PostMapping("/disponibilidad")
    @Operation(summary = "Crear disponibilidad horaria (CUS_17)")
    public ResponseEntity<ApiResponse<DisponibilidadDTO>> crearDisponibilidad(
            @Valid @RequestBody DisponibilidadDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Disponibilidad creada",
                doctorService.crearDisponibilidad(dto)));
    }

    @PutMapping("/disponibilidad/{id}")
    @Operation(summary = "Actualizar disponibilidad horaria (CUS_17)")
    public ResponseEntity<ApiResponse<DisponibilidadDTO>> actualizarDisponibilidad(
            @PathVariable Integer id, @Valid @RequestBody DisponibilidadDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Disponibilidad actualizada",
                doctorService.actualizarDisponibilidad(id, dto)));
    }

    @DeleteMapping("/disponibilidad/{id}")
    @Operation(summary = "Eliminar disponibilidad horaria (CUS_17)")
    public ResponseEntity<ApiResponse<Void>> eliminarDisponibilidad(@PathVariable Integer id) {
        doctorService.eliminarDisponibilidad(id);
        return ResponseEntity.ok(ApiResponse.ok("Disponibilidad eliminada"));
    }
}

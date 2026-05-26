package edu.upn.clinica.backend.doctor.controller;

import edu.upn.clinica.backend.doctor.dto.DoctorDTO;
import edu.upn.clinica.backend.doctor.service.DoctorService;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/doctores")
@Tag(name = "Admin Doctores", description = "Administración de doctores (CUS_45)")
@SecurityRequirement(name = "bearerAuth")
public class AdminDoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    @Operation(summary = "Listar doctores con datos completos")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.ok("Doctores obtenidos",
                doctorService.listarTodos()));
    }

    @PatchMapping("/{id}/especialidad")
    @Operation(summary = "Actualizar especialidad de un doctor")
    public ResponseEntity<ApiResponse<Void>> actualizarEspecialidad(
            @PathVariable Integer id,
            @Valid @RequestBody EspecialidadRequest request) {
        doctorService.actualizarEspecialidad(id, request.getEspecialidad());
        return ResponseEntity.ok(ApiResponse.ok("Especialidad actualizada correctamente"));
    }

    public static class EspecialidadRequest {
        @NotBlank
        private String especialidad;

        public @NotBlank String getEspecialidad() { return especialidad; }
        public void setEspecialidad(@NotBlank String especialidad) { this.especialidad = especialidad; }
    }
}

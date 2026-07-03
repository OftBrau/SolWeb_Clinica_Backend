package edu.upn.clinica.backend.doctor.controller;

import edu.upn.clinica.backend.doctor.dto.ActualizarDoctorRequest;
import edu.upn.clinica.backend.doctor.dto.CrearDoctorRequest;
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

    @PostMapping
    @Operation(summary = "Crear nuevo doctor (CUS_45)")
    public ResponseEntity<ApiResponse<Void>> crear(@Valid @RequestBody CrearDoctorRequest request) {
        doctorService.registrar(request);
        return ResponseEntity.ok(ApiResponse.ok("Doctor creado correctamente"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un doctor (CUS_45)")
    public ResponseEntity<ApiResponse<Void>> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarDoctorRequest request) {
        doctorService.actualizar(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Doctor actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (desactivar) un doctor")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        doctorService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Doctor desactivado correctamente"));
    }

    @PutMapping("/{id}/activar")
    @Operation(summary = "Reactivar un doctor")
    public ResponseEntity<ApiResponse<Void>> activar(@PathVariable Integer id) {
        doctorService.activar(id);
        return ResponseEntity.ok(ApiResponse.ok("Doctor reactivado correctamente"));
    }

    @PatchMapping("/{id}/destacar")
    @Operation(summary = "Marcar/desmarcar doctor como destacado")
    public ResponseEntity<ApiResponse<Void>> toggleDestacado(@PathVariable Integer id) {
        doctorService.toggleDestacado(id);
        return ResponseEntity.ok(ApiResponse.ok("Destacado actualizado"));
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

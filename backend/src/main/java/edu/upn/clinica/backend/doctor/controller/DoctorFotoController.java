package edu.upn.clinica.backend.doctor.controller;

import edu.upn.clinica.backend.doctor.service.CloudinaryService;
import edu.upn.clinica.backend.doctor.service.DoctorService;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/doctores")
@Tag(name = "Admin Doctores")
@SecurityRequirement(name = "bearerAuth")
public class DoctorFotoController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/{id}/foto")
    @Operation(summary = "Subir foto de perfil del doctor")
    public ResponseEntity<ApiResponse<Map<String, String>>> subirFoto(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) {

        String url = cloudinaryService.subirFoto(file);
        doctorService.actualizarFoto(id, url);
        return ResponseEntity.ok(ApiResponse.ok("Foto subida correctamente",
                Map.of("fotoUrl", url)));
    }
}

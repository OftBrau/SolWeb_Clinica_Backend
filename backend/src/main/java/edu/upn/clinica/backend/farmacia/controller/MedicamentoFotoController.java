package edu.upn.clinica.backend.farmacia.controller;

import edu.upn.clinica.backend.doctor.service.CloudinaryService;
import edu.upn.clinica.backend.farmacia.repository.MedicamentoRepository;
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
@RequestMapping("/api/farmacia/medicamentos")
@Tag(name = "Farmacia - Productos Foto", description = "Subida de fotos de productos")
@SecurityRequirement(name = "bearerAuth")
public class MedicamentoFotoController {

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @PostMapping("/{id}/foto")
    @Operation(summary = "Subir foto del producto a Cloudinary")
    public ResponseEntity<ApiResponse<Map<String, String>>> subirFoto(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.subirFoto(file);
        medicamentoRepository.updateFoto(id, url);
        return ResponseEntity.ok(ApiResponse.ok("Foto subida", Map.of("url", url)));
    }
}

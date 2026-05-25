package edu.upn.clinica.backend.hce.controller;

import edu.upn.clinica.backend.hce.model.HistorialItem;
import edu.upn.clinica.backend.hce.service.HceService;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hce")
@Tag(name = "HCE", description = "Historia Clínica Electrónica del paciente")
public class HceController {

    @Autowired private HceService hceService;

    @GetMapping("/documentos")
    @Operation(summary = "Listar historial clínico del paciente autenticado")
    public ResponseEntity<ApiResponse<List<HistorialItem>>> listar() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return ResponseEntity.ok(ApiResponse.ok("Historial",
                hceService.listarPorEmail(email)));
    }

    @GetMapping("/documentos/{id}/descargar")
    @Operation(summary = "Descargar documento de la HCE (CUS_07)")
    public ResponseEntity<byte[]> descargar(@PathVariable Integer id) {
        String reporte = hceService.generarReporteTexto(id);
        byte[] contenido = reporte.getBytes(java.nio.charset.StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "hce_documento_" + id + ".txt");
        headers.setContentLength(contenido.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(contenido);
    }
}

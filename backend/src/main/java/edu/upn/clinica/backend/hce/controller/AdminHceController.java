package edu.upn.clinica.backend.hce.controller;

import edu.upn.clinica.backend.hce.model.HistorialItem;
import edu.upn.clinica.backend.hce.service.HceService;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/hce")
@Tag(name = "Admin HCE", description = "Administración de Historias Clínicas (CUS_07)")
@SecurityRequirement(name = "bearerAuth")
public class AdminHceController {

    @Autowired private HceService hceService;

    @GetMapping("/documentos")
    @Operation(summary = "Listar todas las historias clínicas (admin)")
    public ResponseEntity<ApiResponse<List<HistorialItem>>> listarTodas() {
        return ResponseEntity.ok(ApiResponse.ok("Historial completo",
                hceService.listarTodos()));
    }

    @GetMapping("/documentos/{id}/descargar-pdf")
    @Operation(summary = "Descargar documento de la HCE en PDF (admin)")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Integer id) {
        byte[] pdf = hceService.generarReportePDF(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "hce_documento_" + id + ".pdf");
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}

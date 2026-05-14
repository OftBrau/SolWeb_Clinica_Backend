package edu.upn.clinica.backend.hce.controller;

import edu.upn.clinica.backend.hce.model.HistorialItem;
import edu.upn.clinica.backend.hce.service.HceService;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
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

    // GET /api/hce/documentos
    @GetMapping("/documentos")
    @Operation(summary = "Listar historial clínico del paciente autenticado")
    public ResponseEntity<ApiResponse<List<HistorialItem>>> listar() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return ResponseEntity.ok(ApiResponse.ok("Historial",
                hceService.listarPorEmail(email)));
    }
}
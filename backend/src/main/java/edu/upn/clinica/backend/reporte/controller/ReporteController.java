package edu.upn.clinica.backend.reporte.controller;

import edu.upn.clinica.backend.reporte.dto.ReporteDiarioDTO;
import edu.upn.clinica.backend.reporte.service.ReporteService;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "Generación de reportes operativos (CUS_39)")
@SecurityRequirement(name = "bearerAuth")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/operativo-diario")
    @Operation(summary = "Generar reporte operativo diario")
    public ResponseEntity<ApiResponse<ReporteDiarioDTO>> reporteDiario(
            @RequestParam(required = false) String fecha) {
        return ResponseEntity.ok(ApiResponse.ok("Reporte diario generado",
                reporteService.generarReporteDiario(fecha)));
    }
}

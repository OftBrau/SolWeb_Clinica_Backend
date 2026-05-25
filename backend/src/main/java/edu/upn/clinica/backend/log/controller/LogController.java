package edu.upn.clinica.backend.log.controller;

import edu.upn.clinica.backend.log.model.LogActividad;
import edu.upn.clinica.backend.log.service.LogService;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/logs")
@Tag(name = "Logs", description = "Consulta de logs de actividad del sistema (CUS_49)")
@SecurityRequirement(name = "bearerAuth")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping
    @Operation(summary = "Listar logs de actividad (paginated)")
    public ResponseEntity<ApiResponse<PageResult<LogActividad>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok("Logs obtenidos",
                logService.listar(page, size)));
    }

    @GetMapping("/usuario")
    @Operation(summary = "Listar logs por email de usuario")
    public ResponseEntity<ApiResponse<PageResult<LogActividad>>> listarPorEmail(
            @RequestParam String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok("Logs del usuario",
                logService.listarPorEmail(email, page, size)));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar logs por acción")
    public ResponseEntity<ApiResponse<PageResult<LogActividad>>> listarPorAccion(
            @RequestParam String accion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.ok("Logs encontrados",
                logService.listarPorAccion(accion, page, size)));
    }
}

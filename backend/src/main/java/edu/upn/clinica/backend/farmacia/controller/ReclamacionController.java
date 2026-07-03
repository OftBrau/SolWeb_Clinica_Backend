package edu.upn.clinica.backend.farmacia.controller;

import edu.upn.clinica.backend.farmacia.dto.CrearReclamacionRequest;
import edu.upn.clinica.backend.farmacia.dto.ReclamacionDTO;
import edu.upn.clinica.backend.farmacia.service.ReclamacionService;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.shared.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/farmacia/reclamaciones")
@Tag(name = "Farmacia - Libro de Reclamaciones", description = "Libro de Reclamaciones")
public class ReclamacionController {

    @Autowired
    private ReclamacionService reclamacionService;
    @Autowired
    private PacienteRepository pacienteRepository;

    @GetMapping
    @Operation(summary = "Listar todas las reclamaciones (admin)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<PageResult<ReclamacionDTO>>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok("Reclamaciones", reclamacionService.listar(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar reclamacion por ID")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<ReclamacionDTO>> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Reclamacion", reclamacionService.buscarPorId(id)));
    }

    @GetMapping("/mis-reclamaciones")
    @Operation(summary = "Listar reclamaciones del paciente autenticado")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<List<ReclamacionDTO>>> misReclamaciones(Authentication auth) {
        Integer idPaciente = obtenerIdPaciente(auth);
        return ResponseEntity.ok(ApiResponse.ok("Mis reclamaciones",
                reclamacionService.listarPorPaciente(idPaciente)));
    }

    @PostMapping
    @Operation(summary = "Crear reclamacion (paciente autenticado)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<ReclamacionDTO>> crear(
            Authentication auth,
            @Valid @RequestBody CrearReclamacionRequest request) {
        Integer idPaciente = obtenerIdPaciente(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Reclamacion registrada",
                        reclamacionService.crear(idPaciente, request)));
    }

    @PostMapping("/publico")
    @Operation(summary = "Crear reclamacion anonima (sin login)")
    public ResponseEntity<ApiResponse<ReclamacionDTO>> crearAnonimo(
            @Valid @RequestBody CrearReclamacionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Reclamacion registrada",
                        reclamacionService.crearAnonimo(request)));
    }

    @PutMapping("/{id}/responder")
    @Operation(summary = "Responder/actualizar estado de reclamacion (admin)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<ReclamacionDTO>> responder(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String estado = body.getOrDefault("estado", "RESUELTO");
        String respuesta = body.getOrDefault("respuesta", "");
        return ResponseEntity.ok(ApiResponse.ok("Reclamacion actualizada",
                reclamacionService.responder(id, estado, respuesta)));
    }

    private Integer obtenerIdPaciente(Authentication auth) {
        String email = auth.getName();
        return pacienteRepository.findByEmail(email)
                .map(p -> p.getIdPaciente())
                .orElseThrow(() -> new AppException("Paciente no encontrado", HttpStatus.NOT_FOUND));
    }
}

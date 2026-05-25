package edu.upn.clinica.backend.examen.controller;

import edu.upn.clinica.backend.examen.dto.ExamenRequestDTO;
import edu.upn.clinica.backend.examen.dto.ExamenResponseDTO;
import edu.upn.clinica.backend.examen.service.ExamenService;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/examenes")
@Tag(name = "Exámenes", description = "Solicitud de exámenes de laboratorio e imagen (CUS_21/22)")
@SecurityRequirement(name = "bearerAuth")
public class ExamenController {

    @Autowired
    private ExamenService examenService;

    @PostMapping
    @Operation(summary = "Solicitar examen (laboratorio o imagen)")
    public ResponseEntity<ApiResponse<ExamenResponseDTO>> solicitar(
            @Valid @RequestBody ExamenRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Integer idDoctor = Integer.parseInt(auth.getCredentials() != null ? auth.getCredentials().toString() : "0");
        return ResponseEntity.ok(ApiResponse.ok("Examen solicitado",
                examenService.solicitar(dto, idDoctor)));
    }

    @GetMapping("/paciente/{idPaciente}")
    @Operation(summary = "Listar exámenes por paciente")
    public ResponseEntity<ApiResponse<List<ExamenResponseDTO>>> listarPorPaciente(
            @PathVariable Integer idPaciente) {
        return ResponseEntity.ok(ApiResponse.ok("Exámenes del paciente",
                examenService.listarPorPaciente(idPaciente)));
    }

    @GetMapping("/doctor/{idDoctor}")
    @Operation(summary = "Listar exámenes solicitados por un doctor")
    public ResponseEntity<ApiResponse<List<ExamenResponseDTO>>> listarPorDoctor(
            @PathVariable Integer idDoctor) {
        return ResponseEntity.ok(ApiResponse.ok("Exámenes del doctor",
                examenService.listarPorDoctor(idDoctor)));
    }

    @GetMapping("/consulta/{idConsulta}")
    @Operation(summary = "Listar exámenes de una consulta")
    public ResponseEntity<ApiResponse<List<ExamenResponseDTO>>> listarPorConsulta(
            @PathVariable Integer idConsulta) {
        return ResponseEntity.ok(ApiResponse.ok("Exámenes de la consulta",
                examenService.listarPorConsulta(idConsulta)));
    }

    @PutMapping("/{id}/resultado")
    @Operation(summary = "Registrar resultado de examen")
    public ResponseEntity<ApiResponse<ExamenResponseDTO>> registrarResultado(
            @PathVariable Integer id, @RequestBody String resultado) {
        return ResponseEntity.ok(ApiResponse.ok("Resultado registrado",
                examenService.registrarResultado(id, resultado)));
    }
}

package edu.upn.clinica.backend.consulta.controller;

import edu.upn.clinica.backend.consulta.dto.ConsultaRequestDTO;
import edu.upn.clinica.backend.consulta.dto.ConsultaResponseDTO;
import edu.upn.clinica.backend.consulta.service.ConsultaService;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultas")
@Tag(name = "Consultas Médicas", description = "Diagnóstico, tratamiento y prescripción")
@SecurityRequirement(name = "bearerAuth")
public class ConsultaController {

    @Autowired private ConsultaService  consultaService;
    @Autowired private DoctorRepository doctorRepository;

    @PostMapping
    @Operation(summary = "Iniciar consulta médica (asociada a una cita)")
    public ResponseEntity<ApiResponse<ConsultaResponseDTO>> iniciar(
            @Valid @RequestBody ConsultaRequestDTO req) {
        Integer idDoctor = getDoctorId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Consulta iniciada",
                        consultaService.iniciar(idDoctor, req)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una consulta")
    public ResponseEntity<ApiResponse<ConsultaResponseDTO>> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Consulta encontrada",
                consultaService.obtenerPorId(id, getDoctorId())));
    }

    @PutMapping("/{id}/diagnostico")
    @Operation(summary = "Registrar diagnóstico (CIE-10 + descripción)")
    public ResponseEntity<ApiResponse<ConsultaResponseDTO>> registrarDiagnostico(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok("Diagnóstico registrado",
                consultaService.registrarDiagnostico(id, getDoctorId(),
                        body.get("diagnosticoCie10"), body.get("descripcionDiagnostico"))));
    }

    @PutMapping("/{id}/tratamiento")
    @Operation(summary = "Registrar tratamiento")
    public ResponseEntity<ApiResponse<ConsultaResponseDTO>> registrarTratamiento(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok("Tratamiento registrado",
                consultaService.registrarTratamiento(id, getDoctorId(),
                        body.get("tratamiento"))));
    }

    @PutMapping("/{id}/prescripcion")
    @Operation(summary = "Prescribir medicamentos")
    public ResponseEntity<ApiResponse<ConsultaResponseDTO>> prescribir(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.ok("Prescripción registrada",
                consultaService.prescribir(id, getDoctorId(),
                        body.get("prescripcion"))));
    }

    @GetMapping("/paciente/{idPaciente}")
    @Operation(summary = "Listar consultas de un paciente")
    public ResponseEntity<ApiResponse<List<ConsultaResponseDTO>>> listarPorPaciente(
            @PathVariable Integer idPaciente) {
        return ResponseEntity.ok(ApiResponse.ok("Consultas del paciente",
                consultaService.listarPorPaciente(idPaciente, getDoctorId())));
    }

    private Integer getDoctorId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return doctorRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException(
                        "Doctor no encontrado para el usuario autenticado",
                        HttpStatus.NOT_FOUND));
    }
}

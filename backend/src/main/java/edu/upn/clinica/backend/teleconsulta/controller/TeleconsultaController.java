package edu.upn.clinica.backend.teleconsulta.controller;

import edu.upn.clinica.backend.auth.UsuarioRepository;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.teleconsulta.dto.SolicitarTeleconsultaRequest;
import edu.upn.clinica.backend.teleconsulta.dto.TeleconsultaDTO;
import edu.upn.clinica.backend.teleconsulta.service.TeleconsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teleconsulta")
@Tag(name = "Teleconsultas", description = "Gestión de teleconsultas")
public class TeleconsultaController {

    @Autowired private TeleconsultaService teleconsultaService;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @GetMapping("/mis-teleconsultas")
    @Operation(summary = "Listar teleconsultas según el rol")
    public ResponseEntity<ApiResponse<List<TeleconsultaDTO>>> listar() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        boolean isPaciente = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE"));
        boolean isDoctor = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));

        if (isPaciente) {
            Integer idPac = pacienteRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException("Paciente no encontrado", HttpStatus.NOT_FOUND))
                    .getIdPaciente();
            return ResponseEntity.ok(ApiResponse.ok("Teleconsultas",
                    teleconsultaService.listarPorPaciente(idPac)));
        }

        if (isDoctor) {
            Integer idDoc = doctorRepository.findIdByEmail(email)
                    .orElseThrow(() -> new AppException("Doctor no encontrado", HttpStatus.NOT_FOUND));
            return ResponseEntity.ok(ApiResponse.ok("Teleconsultas",
                    teleconsultaService.listarPorDoctor(idDoc)));
        }

        return ResponseEntity.ok(ApiResponse.ok("Teleconsultas",
                teleconsultaService.listarTodas()));
    }

    @PostMapping("/solicitar")
    @Operation(summary = "Solicitar una nueva teleconsulta")
    public ResponseEntity<ApiResponse<TeleconsultaDTO>> solicitar(
            @Valid @RequestBody SolicitarTeleconsultaRequest req) {
        Integer idPaciente = getIdPaciente();
        TeleconsultaDTO dto = teleconsultaService.solicitar(idPaciente, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Teleconsulta solicitada", dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de una teleconsulta")
    public ResponseEntity<ApiResponse<TeleconsultaDTO>> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Teleconsulta encontrada",
                teleconsultaService.obtenerPorId(id)));
    }

    private Integer getIdPaciente() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return pacienteRepository.findByEmail(email)
                .map(p -> p.getIdPaciente())
                .orElseThrow(() -> new AppException(
                        "Paciente no encontrado", HttpStatus.NOT_FOUND));
    }
}

package edu.upn.clinica.backend.practicante.controller;

import edu.upn.clinica.backend.hce.model.HistorialItem;
import edu.upn.clinica.backend.practicante.dto.ActividadDTO;
import edu.upn.clinica.backend.practicante.dto.ConsultaPracDTO;
import edu.upn.clinica.backend.practicante.dto.EvaluacionDTO;
import edu.upn.clinica.backend.practicante.dto.PacienteAsignadoDTO;
import edu.upn.clinica.backend.practicante.service.PracticanteService;
import edu.upn.clinica.backend.shared.ApiResponse;
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

@RestController
@RequestMapping("/api/practicante")
@Tag(name = "Practicante", description = "Operaciones del módulo practicante (CUS_25/26/27/28/29/30/31)")
@SecurityRequirement(name = "bearerAuth")
public class PracticanteController {

    @Autowired
    private PracticanteService practicanteService;

    @GetMapping("/actividades")
    @Operation(summary = "Listar actividades del practicante (CUS_30)")
    public ResponseEntity<ApiResponse<List<ActividadDTO>>> listarActividades(
            @RequestParam String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR") || a.getAuthority().equals("ROLE_DIRECTOR"));

        if (isAdmin) {
            return ResponseEntity.ok(ApiResponse.ok("Actividades obtenidas",
                    practicanteService.listarTodasLasActividades()));
        }
        return ResponseEntity.ok(ApiResponse.ok("Actividades obtenidas",
                practicanteService.listarActividades(email)));
    }

    @GetMapping("/actividades/{id}")
    @Operation(summary = "Obtener detalle de actividad (CUS_31)")
    public ResponseEntity<ApiResponse<ActividadDTO>> obtenerActividad(
            @PathVariable Integer id, @RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.ok("Actividad obtenida",
                practicanteService.obtenerActividad(id, email)));
    }

    @PostMapping("/consultas")
    @Operation(summary = "Registrar consulta bajo supervisión (CUS_25)")
    public ResponseEntity<ApiResponse<ConsultaPracDTO>> registrarConsulta(
            @Valid @RequestBody ConsultaPracDTO dto,
            @RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.ok("Consulta registrada",
                practicanteService.registrarConsulta(dto, email)));
    }

    @PutMapping("/consultas/{id}/enviar-revision")
    @Operation(summary = "Enviar consulta a revisión del doctor (CUS_26)")
    public ResponseEntity<ApiResponse<Void>> enviarARevision(
            @PathVariable Integer id, @RequestParam String email) {
        practicanteService.enviarARevision(id, email);
        return ResponseEntity.ok(ApiResponse.ok("Consulta enviada a revisión"));
    }

    @GetMapping("/evaluaciones")
    @Operation(summary = "Listar evaluaciones recibidas (CUS_29)")
    public ResponseEntity<ApiResponse<List<EvaluacionDTO>>> listarEvaluaciones(
            @RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.ok("Evaluaciones obtenidas",
                practicanteService.listarEvaluaciones(email)));
    }

    @GetMapping("/pacientes-asignados")
    @Operation(summary = "Buscar historia clínica de paciente asignado (CUS_27)")
    public ResponseEntity<ApiResponse<List<PacienteAsignadoDTO>>> buscarPacientes(
            @RequestParam String email, @RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.ok("Pacientes encontrados",
                practicanteService.buscarPacientesAsignados(email, q)));
    }

    @GetMapping("/hce/{idPaciente}")
    @Operation(summary = "Consultar detalle de historia clínica de paciente asignado (CUS_28)")
    public ResponseEntity<ApiResponse<List<HistorialItem>>> verHistoriaClinica(
            @PathVariable Integer idPaciente, @RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.ok("Historia clínica obtenida",
                practicanteService.verHistoriaClinica(idPaciente, email)));
    }
}

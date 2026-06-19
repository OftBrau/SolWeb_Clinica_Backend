package edu.upn.clinica.backend.practicante.controller;

import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.practicante.dto.ActividadDTO;
import edu.upn.clinica.backend.practicante.dto.CrearActividadRequest;
import edu.upn.clinica.backend.practicante.dto.CrearEvaluacionRequest;
import edu.upn.clinica.backend.practicante.dto.EvaluacionDTO;
import edu.upn.clinica.backend.practicante.model.PracticanteEntidad;
import edu.upn.clinica.backend.practicante.service.PracticanteService;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
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
@RequestMapping("/api/doctor/practicantes")
@Tag(name = "Doctor Practicantes", description = "Gestión de practicantes asignados al doctor (CUS_25/26/29/30/31)")
@SecurityRequirement(name = "bearerAuth")
public class DoctorPracticanteController {

    @Autowired
    private PracticanteService practicanteService;

    @Autowired
    private DoctorRepository doctorRepository;

    private Integer obtenerIdDoctor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return doctorRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException("Doctor no encontrado", HttpStatus.NOT_FOUND));
    }

    @GetMapping("/mis-practicantes")
    @Operation(summary = "Listar practicantes asignados al doctor autenticado")
    public ResponseEntity<ApiResponse<List<PracticanteEntidad>>> listarMisPracticantes() {
        Integer idDoctor = obtenerIdDoctor();
        return ResponseEntity.ok(ApiResponse.ok("Practicantes obtenidos",
                practicanteService.listarMisPracticantes(idDoctor)));
    }

    @GetMapping("/actividades")
    @Operation(summary = "Listar actividades creadas por el doctor para sus practicantes")
    public ResponseEntity<ApiResponse<List<ActividadDTO>>> listarActividades() {
        Integer idDoctor = obtenerIdDoctor();
        return ResponseEntity.ok(ApiResponse.ok("Actividades obtenidas",
                practicanteService.listarActividadesComoSupervisor(idDoctor)));
    }

    @PostMapping("/actividades")
    @Operation(summary = "Crear actividad/tarea para un practicante asignado")
    public ResponseEntity<ApiResponse<ActividadDTO>> crearActividad(
            @Valid @RequestBody CrearActividadRequest request) {
        Integer idDoctor = obtenerIdDoctor();
        return ResponseEntity.ok(ApiResponse.ok("Actividad creada",
                practicanteService.crearActividadParaPracticante(request, idDoctor)));
    }

    @GetMapping("/evaluaciones")
    @Operation(summary = "Listar evaluaciones hechas por el doctor a sus practicantes")
    public ResponseEntity<ApiResponse<List<EvaluacionDTO>>> listarEvaluaciones() {
        Integer idDoctor = obtenerIdDoctor();
        return ResponseEntity.ok(ApiResponse.ok("Evaluaciones obtenidas",
                practicanteService.listarEvaluacionesComoSupervisor(idDoctor)));
    }

    @PostMapping("/evaluaciones")
    @Operation(summary = "Evaluar a un practicante asignado")
    public ResponseEntity<ApiResponse<EvaluacionDTO>> evaluarPracticante(
            @Valid @RequestBody CrearEvaluacionRequest request) {
        Integer idDoctor = obtenerIdDoctor();
        return ResponseEntity.ok(ApiResponse.ok("Evaluación registrada",
                practicanteService.evaluarPracticante(request, idDoctor)));
    }
}

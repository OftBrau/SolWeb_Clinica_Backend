package edu.upn.clinica.backend.cita.controller;

import edu.upn.clinica.backend.cita.dto.CitaPublicaRequest;
import edu.upn.clinica.backend.cita.dto.CitaPublicaResponse;
import edu.upn.clinica.backend.cita.service.CitaPublicaService;
import edu.upn.clinica.backend.doctor.dto.DoctorDisponibleDTO;
import edu.upn.clinica.backend.especialidad.dto.EspecialidadDTO;
import edu.upn.clinica.backend.especialidad.service.EspecialidadService;
import edu.upn.clinica.backend.paciente.model.Paciente;
import edu.upn.clinica.backend.shared.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/cita-publica")
@Tag(name = "Cita Pública", description = "Endpoints públicos para agendar citas desde la landing page")
public class CitaPublicaController {

    @Autowired
    private CitaPublicaService citaPublicaService;

    @Autowired
    private EspecialidadService especialidadService;

    @GetMapping("/especialidades")
    @Operation(summary = "Listar especialidades activas para el formulario público")
    public ResponseEntity<ApiResponse<List<EspecialidadDTO>>> listarEspecialidades() {
        return ResponseEntity.ok(ApiResponse.ok("Especialidades obtenidas",
                especialidadService.listarActivas()));
    }

    @GetMapping("/doctores")
    @Operation(summary = "Listar todos los doctores activos (para landing page)")
    public ResponseEntity<ApiResponse<List<DoctorDisponibleDTO>>> listarTodosDoctores() {
        List<DoctorDisponibleDTO> doctores = citaPublicaService.listarTodosDoctores();
        return ResponseEntity.ok(ApiResponse.ok("Doctores obtenidos", doctores));
    }

    @GetMapping("/doctores/{especialidad}")
    @Operation(summary = "Listar doctores disponibles por especialidad")
    public ResponseEntity<ApiResponse<List<DoctorDisponibleDTO>>> listarDoctores(
            @PathVariable String especialidad) {

        List<DoctorDisponibleDTO> doctores =
                citaPublicaService.listarDoctoresPorEspecialidad(especialidad);
        return ResponseEntity.ok(ApiResponse.ok("Doctores disponibles", doctores));
    }

    // ─────────────────────────────────────────────
    //  GET /api/cita-publica/buscar-paciente
    //  Busca paciente por email + código de estudiante
    //  Paso 'existente' del formulario Angular
    // ─────────────────────────────────────────────
    @GetMapping("/buscar-paciente")
    @Operation(summary = "Buscar paciente por email")
    public ResponseEntity<ApiResponse<Paciente>> buscarPaciente(
            @RequestParam String email) {

        Paciente paciente = citaPublicaService.buscarPaciente(email);
        return ResponseEntity.ok(ApiResponse.ok("Paciente encontrado", paciente));
    }

    // ─────────────────────────────────────────────
    //  POST /api/cita-publica/agendar
    //  Registra la cita (y el paciente si es nuevo)
    //  Paso final 'cita' → 'exito' del formulario Angular
    // ─────────────────────────────────────────────
    @PostMapping("/agendar")
    @Operation(summary = "Agendar una cita médica (paciente nuevo o existente)")
    public ResponseEntity<ApiResponse<CitaPublicaResponse>> agendar(
            @Valid @RequestBody CitaPublicaRequest request) {

        CitaPublicaResponse response = citaPublicaService.agendar(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cita agendada exitosamente", response));
    }

    @PostMapping("/reservar-basica")
    @Operation(summary = "Reservar cita basica - sistema asigna doctor automatico (publico)")
    public ResponseEntity<ApiResponse<CitaPublicaResponse>> reservarBasica(
            @RequestBody Map<String, Object> body) {
        CitaPublicaResponse response = citaPublicaService.reservarBasicaPublica(body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cita basica reservada correctamente", response));
    }

    @PostMapping("/reservar-especialista")
    @Operation(summary = "Reservar cita con especialista - requiere asignacion del asistente (publico)")
    public ResponseEntity<ApiResponse<CitaPublicaResponse>> reservarEspecialista(
            @RequestBody Map<String, Object> body) {
        CitaPublicaResponse response = citaPublicaService.reservarEspecialistaPublica(body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Solicitud de cita especialista registrada", response));
    }
}
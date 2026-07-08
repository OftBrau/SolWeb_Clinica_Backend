package edu.upn.clinica.backend.asistente.controller;

import edu.upn.clinica.backend.asistente.dto.AsignarCitaRequest;
import edu.upn.clinica.backend.asistente.model.CitaPendiente;
import edu.upn.clinica.backend.asistente.service.AsistenteService;
import edu.upn.clinica.backend.auth.UsuarioRepository;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asistente")
@Tag(name = "Asistente", description = "Gestion de citas por el asistente")
@SecurityRequirement(name = "bearerAuth")
public class AsistenteController {

    @Autowired
    private AsistenteService asistenteService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/citas/pendientes")
    @Operation(summary = "Listar citas pendientes de asignacion")
    public ResponseEntity<ApiResponse<List<CitaPendiente>>> listarPendientes() {
        return ResponseEntity.ok(ApiResponse.ok("Citas pendientes obtenidas",
                asistenteService.listarPendientes()));
    }

    @GetMapping("/doctores/disponibles")
    @Operation(summary = "Listar doctores disponibles para una especialidad, fecha y hora")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listarDoctoresDisponibles(
            @RequestParam Integer idEspecialidad,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora) {
        return ResponseEntity.ok(ApiResponse.ok("Doctores disponibles",
                asistenteService.listarDoctoresDisponibles(fecha, hora, idEspecialidad)));
    }

    @PostMapping("/citas/{id}/asignar-doctor")
    @Operation(summary = "Asignar doctor y consultorio a una cita pendiente")
    public ResponseEntity<ApiResponse<Void>> asignarDoctor(
            @PathVariable Integer id,
            @Valid @RequestBody AsignarCitaRequest request) {
        Integer idAsistente = getIdUsuario();
        asistenteService.asignarDoctor(id, request.getIdDoctor(),
                request.getIdConsultorio(), idAsistente);
        return ResponseEntity.ok(ApiResponse.ok("Cita asignada correctamente"));
    }

    @PatchMapping("/citas/{id}/rechazar")
    @Operation(summary = "Rechazar solicitud de cita especialista")
    public ResponseEntity<ApiResponse<Void>> rechazarCita(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        asistenteService.rechazarCita(id, body.get("motivo"));
        return ResponseEntity.ok(ApiResponse.ok("Cita rechazada correctamente"));
    }

    @GetMapping("/agenda-diaria")
    @Operation(summary = "Ver agenda diaria completa")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> agendaDiaria(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(ApiResponse.ok("Agenda diaria",
                asistenteService.listarAgendaDiaria(fecha)));
    }

    @GetMapping("/doctores")
    @Operation(summary = "Listar todos los doctores")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listarDoctores() {
        return ResponseEntity.ok(ApiResponse.ok("Doctores",
                asistenteService.listarDoctores()));
    }

    @GetMapping("/especialidades")
    @Operation(summary = "Listar especialidades activas con costo extra")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listarEspecialidades() {
        return ResponseEntity.ok(ApiResponse.ok("Especialidades",
                asistenteService.listarEspecialidadesActivas()));
    }

    @GetMapping("/consultorios/disponibles")
    @Operation(summary = "Listar consultorios disponibles en fecha y hora")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> consultoriosDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora) {
        return ResponseEntity.ok(ApiResponse.ok("Consultorios disponibles",
                asistenteService.listarConsultoriosDisponibles(fecha, hora)));
    }

    private Integer getIdUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioRepository.findByEmail(email)
                .map(u -> u.getId())
                .orElseThrow(() -> new AppException(
                        "Usuario no encontrado", HttpStatus.NOT_FOUND));
    }
}

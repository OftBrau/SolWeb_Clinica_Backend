package edu.upn.clinica.backend.reserva.controller;

import edu.upn.clinica.backend.cita.dto.CitaPublicaResponse;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.reserva.dto.ReservaBasicaRequest;
import edu.upn.clinica.backend.reserva.dto.ReservaEspecialistaRequest;
import edu.upn.clinica.backend.reserva.service.ReservaService;
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

@RestController
@RequestMapping("/api/paciente/reservas")
@Tag(name = "Reserva de Citas", description = "Reserva de citas basicas y con especialista")
@SecurityRequirement(name = "bearerAuth")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private PacienteRepository pacienteRepository;

    @PostMapping("/basica")
    @Operation(summary = "Reservar cita basica - sistema asigna doctor automaticamente")
    public ResponseEntity<ApiResponse<CitaPublicaResponse>> reservarBasica(
            @Valid @RequestBody ReservaBasicaRequest request) {
        Integer idPaciente = getIdPaciente();
        CitaPublicaResponse cita = reservaService.reservarBasica(
                idPaciente, request.getFecha(), request.getHora(),
                request.getMotivo(), request.getTipo());
        return ResponseEntity.ok(ApiResponse.ok("Cita basica reservada correctamente", cita));
    }

    @PostMapping("/especialista")
    @Operation(summary = "Solicitar cita con especialista - requiere asignacion del asistente")
    public ResponseEntity<ApiResponse<CitaPublicaResponse>> reservarEspecialista(
            @Valid @RequestBody ReservaEspecialistaRequest request) {
        Integer idPaciente = getIdPaciente();
        CitaPublicaResponse cita = reservaService.reservarEspecialista(
                idPaciente, request.getIdEspecialidad(), request.getFecha(),
                request.getHora(), request.getMotivo(), request.getTipo());
        return ResponseEntity.ok(ApiResponse.ok("Solicitud de cita especialista registrada. Pendiente de asignacion.", cita));
    }

    private Integer getIdPaciente() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return pacienteRepository
                .findByEmail(email)
                .map(p -> p.getIdPaciente())
                .orElseThrow(() -> new AppException(
                        "Paciente no encontrado", HttpStatus.NOT_FOUND));
    }
}

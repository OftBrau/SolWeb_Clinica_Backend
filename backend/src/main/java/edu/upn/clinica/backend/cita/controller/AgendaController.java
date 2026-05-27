package edu.upn.clinica.backend.cita.controller;

import edu.upn.clinica.backend.cita.dto.AgendaItemResponse;
import edu.upn.clinica.backend.cita.service.AgendaService;
import edu.upn.clinica.backend.doctor.dto.DoctorDisponibleDTO;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
@Tag(name = "Agenda Médica", description = "Gestión de agenda de consultas del médico")
@SecurityRequirement(name = "bearerAuth")
public class AgendaController {

    @Autowired private AgendaService    agendaService;
    @Autowired private DoctorRepository doctorRepository;

    @GetMapping("/agenda")
    @Operation(summary = "Ver agenda — DOCTOR ve sus citas, ADMIN/DIRECTOR ven todas")
    public ResponseEntity<ApiResponse<List<AgendaItemResponse>>> verAgenda(
            @RequestParam(required = false) String fecha,
            @RequestParam(required = false) Integer idDoctor) {

        LocalDate dia = (fecha != null) ? LocalDate.parse(fecha) : LocalDate.now(ZoneId.of("America/Lima"));
        boolean esAdmin = tieneRol("ADMINISTRADOR") || tieneRol("DIRECTOR");

        List<AgendaItemResponse> agenda;
        if (esAdmin) {
            Integer doctorFiltro = idDoctor;
            if (doctorFiltro != null) {
                agenda = agendaService.verAgenda(doctorFiltro, dia);
            } else {
                agenda = agendaService.verTodasLasAgendas(dia);
            }
        } else {
            Integer idDoc = getDoctorId();
            agenda = agendaService.verAgenda(idDoc, dia);
        }

        return ResponseEntity.ok(ApiResponse.ok("Agenda obtenida", agenda));
    }

    @GetMapping("/doctores")
    @Operation(summary = "Listar doctores (para filtro de agenda de admin/director)")
    public ResponseEntity<ApiResponse<List<DoctorDisponibleDTO>>> listarDoctores() {
        List<DoctorDisponibleDTO> doctores = doctorRepository.findAll();
        return ResponseEntity.ok(ApiResponse.ok("Doctores obtenidos", doctores));
    }

    private Integer getDoctorId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return doctorRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException(
                        "Doctor no encontrado para el usuario autenticado",
                        HttpStatus.NOT_FOUND));
    }

    private boolean tieneRol(String rol) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_" + rol));
    }
}

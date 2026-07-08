package edu.upn.clinica.backend.enfermero.controller;

import edu.upn.clinica.backend.cita.model.Cita;
import edu.upn.clinica.backend.cita.repository.CitaRepository;
import edu.upn.clinica.backend.enfermero.dto.TriajeRequest;
import edu.upn.clinica.backend.enfermero.dto.TriajeResponse;
import edu.upn.clinica.backend.enfermero.model.AsignacionEnfermero;
import edu.upn.clinica.backend.enfermero.repository.EnfermeroRepository;
import edu.upn.clinica.backend.enfermero.service.EnfermeroService;
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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/enfermero")
@Tag(name = "Enfermero", description = "Gestion de triaje y agenda del enfermero")
@SecurityRequirement(name = "bearerAuth")
public class EnfermeroController {

    @Autowired
    private EnfermeroService enfermeroService;

    @Autowired
    private EnfermeroRepository enfermeroRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CitaRepository citaRepository;

    @GetMapping("/mi-agenda")
    @Operation(summary = "Ver citas del doctor asignado en una fecha")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> miAgenda(
            @RequestParam(required = false) String fecha) {
        Integer idEnfermero = getIdEnfermero();
        Integer idDoctor = enfermeroService.obtenerDoctorDeEnfermero(idEnfermero)
                .orElseThrow(() -> new AppException("No tienes un doctor asignado", HttpStatus.NOT_FOUND));

        LocalDate dia = fecha != null ? LocalDate.parse(fecha) : LocalDate.now();
        List<Cita> citas = citaRepository.findByDoctorAndFecha(idDoctor, dia);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Cita c : citas) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("idCita", c.getIdCita());
            item.put("hora", c.getHora().toString());
            item.put("estado", c.getEstado());
            item.put("motivo", c.getMotivo());
            item.put("idPaciente", c.getIdPaciente());
            String nombre = "Paciente #" + c.getIdPaciente();
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "SELECT CONCAT(u.nombre,' ',u.apellido) AS n FROM pacientes p " +
                     "JOIN usuarios u ON p.id_usuario = u.id_usuario WHERE p.id_paciente = ?")) {
                ps.setInt(1, c.getIdPaciente());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) nombre = rs.getString("n");
                }
            } catch (Exception ignored) {}
            item.put("paciente", nombre);
            result.add(item);
        }
        return ResponseEntity.ok(ApiResponse.ok("Agenda del dia", result));
    }

    @GetMapping("/mi-doctor")
    @Operation(summary = "Obtener el doctor al que esta asignado")
    public ResponseEntity<ApiResponse<Map<String, Object>>> miDoctor() {
        Integer idEnfermero = getIdEnfermero();
        Integer idDoctor = enfermeroService.obtenerDoctorDeEnfermero(idEnfermero)
                .orElseThrow(() -> new AppException("No tienes un doctor asignado", HttpStatus.NOT_FOUND));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("idDoctor", idDoctor);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT CONCAT(u.nombre,' ',u.apellido) AS nombre, d.especialidad " +
                 "FROM doctores d JOIN usuarios u ON d.id_usuario = u.id_usuario WHERE d.id_doctor = ?")) {
            ps.setInt(1, idDoctor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.put("nombreDoctor", rs.getString("nombre"));
                    result.put("especialidad", rs.getString("especialidad"));
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando datos doctor: " + e.getMessage());
        }
        return ResponseEntity.ok(ApiResponse.ok("Doctor asignado", result));
    }

    @PostMapping("/triaje")
    @Operation(summary = "Registrar triaje (signos vitales) de una cita")
    public ResponseEntity<ApiResponse<TriajeResponse>> registrarTriaje(
            @Valid @RequestBody TriajeRequest request) {
        Integer idEnfermero = getIdEnfermero();
        TriajeResponse triaje = enfermeroService.registrarTriaje(request, idEnfermero);
        return ResponseEntity.ok(ApiResponse.ok("Triaje registrado correctamente", triaje));
    }

    @GetMapping("/triaje/{idCita}")
    @Operation(summary = "Obtener triaje de una cita")
    public ResponseEntity<ApiResponse<TriajeResponse>> obtenerTriaje(@PathVariable Integer idCita) {
        return ResponseEntity.ok(ApiResponse.ok("Triaje obtenido",
                enfermeroService.obtenerTriaje(idCita)));
    }

    @GetMapping("/practicantes")
    @Operation(summary = "Listar practicantes del mismo doctor")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> listarPracticantes() {
        Integer idEnfermero = getIdEnfermero();
        Integer idDoctor = enfermeroService.obtenerDoctorDeEnfermero(idEnfermero)
                .orElseThrow(() -> new AppException("No tienes un doctor asignado", HttpStatus.NOT_FOUND));

        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT sp.id_practicante, CONCAT(u.nombre,' ',u.apellido) AS nombre, u.email " +
                 "FROM supervision_practicantes sp " +
                 "JOIN doctores d ON sp.id_practicante = d.id_doctor " +
                 "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
                 "WHERE sp.id_supervisor = ?")) {
            ps.setInt(1, idDoctor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("idPracticante", rs.getInt("id_practicante"));
                    m.put("nombre", rs.getString("nombre"));
                    m.put("email", rs.getString("email"));
                    result.add(m);
                }
            }
        } catch (Exception e) {
            System.err.println("Error listando practicantes: " + e.getMessage());
        }
        return ResponseEntity.ok(ApiResponse.ok("Practicantes", result));
    }

    private Integer getIdEnfermero() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<Integer> opt = enfermeroRepository.findEnfermeroIdByEmail(email);
        if (opt.isPresent()) return opt.get();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT IGNORE INTO doctores (id_usuario, especialidad, CMP) " +
                 "SELECT id_usuario, 'Enfermería', CONCAT('ENF-', LPAD(id_usuario, 6, '0')) " +
                 "FROM usuarios WHERE email = ?")) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error creando doctores para enfermero: " + e.getMessage());
        }

        return enfermeroRepository.findEnfermeroIdByEmail(email)
                .orElseThrow(() -> new AppException(
                        "Enfermero no encontrado. Contacta al administrador.",
                        HttpStatus.NOT_FOUND));
    }
}

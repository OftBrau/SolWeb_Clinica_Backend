package edu.upn.clinica.backend.doctor.controller;

import edu.upn.clinica.backend.doctor.dto.DisponibilidadDTO;
import edu.upn.clinica.backend.doctor.service.DoctorService;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/api/doctores")
@Tag(name = "Doctores", description = "Gestión de doctores y disponibilidad horaria")
@SecurityRequirement(name = "bearerAuth")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DataSource dataSource;

    @GetMapping("/mis-pacientes")
    @Operation(summary = "Pacientes del doctor autenticado")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> misPacientes(Authentication auth) {
        String email = auth.getName();
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT DISTINCT p.id_paciente, u.nombre, u.apellido, u.email, u.telefono, " +
                "p.codigo_estudiante, p.fecha_nacimiento, p.genero, p.tipo_sangre, p.alergias, u.estado " +
                "FROM consultas c " +
                "JOIN doctores d ON c.id_doctor = d.id_doctor " +
                "JOIN usuarios du ON d.id_usuario = du.id_usuario " +
                "JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "WHERE du.email = ? ORDER BY u.nombre, u.apellido";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("idPaciente", rs.getInt("id_paciente"));
                    m.put("nombre", rs.getString("nombre"));
                    m.put("apellido", rs.getString("apellido"));
                    m.put("email", rs.getString("email"));
                    m.put("telefono", rs.getString("telefono"));
                    m.put("codigoEstudiante", rs.getString("codigo_estudiante"));
                    m.put("fechaNacimiento", rs.getDate("fecha_nacimiento") != null ? rs.getDate("fecha_nacimiento").toString() : null);
                    m.put("genero", rs.getString("genero"));
                    m.put("tipoSangre", rs.getString("tipo_sangre"));
                    m.put("alergias", rs.getString("alergias"));
                    m.put("estado", rs.getString("estado"));
                    result.add(m);
                }
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return ResponseEntity.ok(ApiResponse.ok("Mis pacientes", result));
    }

    @GetMapping("/{idDoctor}/disponibilidad")
    @Operation(summary = "Listar disponibilidad horaria del doctor (CUS_17)")
    public ResponseEntity<ApiResponse<List<DisponibilidadDTO>>> listarDisponibilidad(
            @PathVariable Integer idDoctor) {
        return ResponseEntity.ok(ApiResponse.ok("Disponibilidad obtenida",
                doctorService.listarDisponibilidad(idDoctor)));
    }

    @PostMapping("/disponibilidad")
    @Operation(summary = "Crear disponibilidad horaria (CUS_17)")
    public ResponseEntity<ApiResponse<DisponibilidadDTO>> crearDisponibilidad(
            @Valid @RequestBody DisponibilidadDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Disponibilidad creada",
                doctorService.crearDisponibilidad(dto)));
    }

    @PutMapping("/disponibilidad/{id}")
    @Operation(summary = "Actualizar disponibilidad horaria (CUS_17)")
    public ResponseEntity<ApiResponse<DisponibilidadDTO>> actualizarDisponibilidad(
            @PathVariable Integer id, @Valid @RequestBody DisponibilidadDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Disponibilidad actualizada",
                doctorService.actualizarDisponibilidad(id, dto)));
    }

    @GetMapping("/mis-practicantes/evaluaciones")
    @Operation(summary = "Practicantes del doctor con perfil para evaluaciones")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> misPracticantesEval(Authentication auth) {
        String email = auth.getName();
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT p.id_practicante, CONCAT(u.nombre,' ',u.apellido) AS nombre, u.email, u.foto_url, " +
                "pp.titulo_profesional, pp.universidad " +
                "FROM supervision_practicantes sp " +
                "JOIN doctores d ON sp.id_supervisor = d.id_doctor " +
                "JOIN usuarios du ON d.id_usuario = du.id_usuario " +
                "LEFT JOIN practicantes p ON sp.id_practicante = p.id_practicante " +
                "LEFT JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "LEFT JOIN perfil_profesional pp ON pp.id_practicante = p.id_practicante AND pp.activo = TRUE " +
                "WHERE du.email = ? ORDER BY u.nombre";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("idPracticante", rs.getInt("id_practicante"));
                    m.put("nombre", rs.getString("nombre"));
                    m.put("email", rs.getString("email"));
                    m.put("fotoUrl", rs.getString("foto_url"));
                    m.put("tituloProfesional", rs.getString("titulo_profesional"));
                    m.put("universidad", rs.getString("universidad"));
                    result.add(m);
                }
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return ResponseEntity.ok(ApiResponse.ok("Mis practicantes", result));
    }

    @DeleteMapping("/disponibilidad/{id}")
    @Operation(summary = "Eliminar disponibilidad horaria (CUS_17)")
    public ResponseEntity<ApiResponse<Void>> eliminarDisponibilidad(@PathVariable Integer id) {
        doctorService.eliminarDisponibilidad(id);
        return ResponseEntity.ok(ApiResponse.ok("Disponibilidad eliminada"));
    }
}

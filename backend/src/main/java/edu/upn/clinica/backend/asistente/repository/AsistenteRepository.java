package edu.upn.clinica.backend.asistente.repository;

import edu.upn.clinica.backend.asistente.model.CitaPendiente;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class AsistenteRepository extends BaseRepository {

    public List<CitaPendiente> findCitasPendientes() {
        String sql = "SELECT c.id_cita, c.id_paciente, c.fecha, c.hora, c.estado, c.tipo, c.motivo, " +
                "c.tipo_reserva, c.monto_extra, c.id_especialidad, " +
                "COALESCE(e.nombre, '') AS nombre_especialidad, " +
                "CONCAT(up.nombre, ' ', up.apellido) AS nombre_paciente, up.email AS email_paciente, " +
                "p.codigo_estudiante " +
                "FROM citas c " +
                "JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                "JOIN usuarios up ON p.id_usuario = up.id_usuario " +
                "LEFT JOIN especialidades e ON c.id_especialidad = e.id_especialidad " +
                "WHERE c.estado = 'PENDIENTE_ASIGNACION' " +
                "ORDER BY c.fecha, c.hora";

        List<CitaPendiente> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapPendiente(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando citas pendientes: " + e.getMessage());
        }
        return lista;
    }

    public List<Map<String, Object>> findDoctoresDisponiblesEspecialidad(
            LocalDate fecha, LocalTime hora, Integer idEspecialidad) {
        String diaSemana = traducirDiaSemana(fecha.getDayOfWeek().getValue());

        String sql = "SELECT d.id_doctor, CONCAT(u.nombre, ' ', u.apellido) AS nombre_doctor, " +
                "d.especialidad, d.foto_url " +
                "FROM doctores d " +
                "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
                "WHERE d.especialidad = (SELECT nombre FROM especialidades WHERE id_especialidad = ?) " +
                "AND u.estado = 'ACTIVO' AND u.rol IN ('DOCTOR', 'MEDICO') " +
                "AND d.id_doctor NOT IN (" +
                "  SELECT c.id_doctor FROM citas c " +
                "  WHERE c.fecha = ? AND c.hora = ? " +
                "  AND c.estado NOT IN ('CANCELADA','NO_ASISTIO')" +
                ") " +
                "ORDER BY u.nombre";

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEspecialidad);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(hora));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("idDoctor", rs.getInt("id_doctor"));
                    m.put("nombreDoctor", rs.getString("nombre_doctor"));
                    m.put("especialidad", rs.getString("especialidad"));
                    m.put("fotoUrl", rs.getString("foto_url"));
                    lista.add(m);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando doctores disponibles: " + e.getMessage());
        }
        return lista;
    }

    public void asignarDoctor(Integer idCita, Integer idDoctor, Integer idConsultorio, Integer idAsistente) {
        String sql = "UPDATE citas SET id_doctor = ?, id_consultorio = ?, id_asistente = ?, " +
                "estado = 'CONFIRMADA' WHERE id_cita = ? AND estado = 'PENDIENTE_ASIGNACION'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            if (idConsultorio != null) ps.setInt(2, idConsultorio);
            else ps.setNull(2, Types.INTEGER);
            ps.setInt(3, idAsistente);
            ps.setInt(4, idCita);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new AppException("La cita ya fue asignada por otro asistente", HttpStatus.CONFLICT);
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error asignando doctor a cita: " + e.getMessage());
        }
    }

    public void rechazarCita(Integer idCita, String motivo) {
        String sql = "UPDATE citas SET estado = 'CANCELADA', motivo = CONCAT(motivo, ' | Rechazado: ', ?) WHERE id_cita = ? AND estado = 'PENDIENTE_ASIGNACION'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, motivo != null ? motivo : "Sin motivo especificado");
            ps.setInt(2, idCita);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new AppException("La cita ya fue gestionada por otro asistente", HttpStatus.CONFLICT);
            }
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error rechazando cita: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> findAgendaDiaria(LocalDate fecha) {
        String sql = "SELECT c.id_cita, c.hora, c.estado, c.tipo, c.motivo, c.tipo_reserva, c.monto_extra, " +
                "CONCAT(up.nombre, ' ', up.apellido) AS paciente, " +
                "CONCAT(ud.nombre, ' ', ud.apellido) AS doctor, " +
                "d.especialidad, " +
                "COALESCE(co.nombre, 'Sin asignar') AS consultorio, " +
                "CONCAT(ua.nombre, ' ', ua.apellido) AS asistente " +
                "FROM citas c " +
                "JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                "JOIN usuarios up ON p.id_usuario = up.id_usuario " +
                "LEFT JOIN doctores d ON c.id_doctor = d.id_doctor " +
                "LEFT JOIN usuarios ud ON d.id_usuario = ud.id_usuario " +
                "LEFT JOIN consultorios co ON c.id_consultorio = co.id_consultorio " +
                "LEFT JOIN usuarios ua ON c.id_asistente = ua.id_usuario " +
                "WHERE c.fecha = ? " +
                "ORDER BY c.hora, d.especialidad";

        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("idCita", rs.getInt("id_cita"));
                    Time h = rs.getTime("hora");
                    m.put("hora", h != null ? h.toLocalTime().toString() : null);
                    m.put("estado", rs.getString("estado"));
                    m.put("tipo", rs.getString("tipo"));
                    m.put("motivo", rs.getString("motivo"));
                    m.put("tipoReserva", rs.getString("tipo_reserva"));
                    m.put("montoExtra", rs.getBigDecimal("monto_extra"));
                    m.put("paciente", rs.getString("paciente"));
                    m.put("doctor", rs.getString("doctor"));
                    m.put("especialidad", rs.getString("especialidad"));
                    m.put("consultorio", rs.getString("consultorio"));
                    m.put("asistente", rs.getString("asistente"));
                    lista.add(m);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando agenda diaria: " + e.getMessage());
        }
        return lista;
    }

    public List<Map<String, Object>> findAllDoctores() {
        String sql = "SELECT d.id_doctor, CONCAT(u.nombre, ' ', u.apellido) AS nombre_doctor, " +
                "d.especialidad " +
                "FROM doctores d " +
                "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
                "WHERE u.estado = 'ACTIVO' AND u.rol IN ('DOCTOR', 'MEDICO') " +
                "ORDER BY u.nombre";
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("idDoctor", rs.getInt("id_doctor"));
                    m.put("nombreDoctor", rs.getString("nombre_doctor"));
                    m.put("especialidad", rs.getString("especialidad"));
                    lista.add(m);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando doctores: " + e.getMessage());
        }
        return lista;
    }

    public List<Map<String, Object>> findConsultoriosDisponibles(LocalDate fecha, LocalTime hora) {
        String diaSemana = traducirDiaSemana(fecha.getDayOfWeek().getValue());
        String sql = "SELECT co.id_consultorio, co.nombre FROM consultorios co " +
                "WHERE co.estado = 'ACTIVO' " +
                "AND co.id_consultorio NOT IN (" +
                "  SELECT ct.id_consultorio FROM citas ct " +
                "  WHERE ct.fecha = ? AND ct.hora = ? AND ct.estado NOT IN ('CANCELADA','NO_ASISTIO')" +
                ") ORDER BY co.nombre";
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setTime(2, Time.valueOf(hora));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("idConsultorio", rs.getInt("id_consultorio"));
                    m.put("nombre", rs.getString("nombre"));
                    lista.add(m);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando consultorios: " + e.getMessage());
        }
        return lista;
    }

    public List<Map<String, Object>> findEspecialidadesActivas() {
        String sql = "SELECT id_especialidad, nombre, costo_extra FROM especialidades WHERE estado = 'ACTIVO' ORDER BY nombre";
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("idEspecialidad", rs.getInt("id_especialidad"));
                    m.put("nombre", rs.getString("nombre"));
                    m.put("costoExtra", rs.getBigDecimal("costo_extra"));
                    lista.add(m);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando especialidades: " + e.getMessage());
        }
        return lista;
    }

    private CitaPendiente mapPendiente(ResultSet rs) throws Exception {
        CitaPendiente cp = new CitaPendiente();
        cp.setIdCita(rs.getInt("id_cita"));
        cp.setIdPaciente(rs.getInt("id_paciente"));
        cp.setNombrePaciente(rs.getString("nombre_paciente"));
        cp.setEmailPaciente(rs.getString("email_paciente"));
        cp.setCodigoEstudiante(rs.getString("codigo_estudiante"));
        cp.setFecha(rs.getDate("fecha").toLocalDate());
        cp.setHora(rs.getTime("hora").toLocalTime());
        cp.setEstado(rs.getString("estado"));
        cp.setTipo(rs.getString("tipo"));
        cp.setMotivo(rs.getString("motivo"));
        cp.setTipoReserva(rs.getString("tipo_reserva"));
        cp.setCostoExtra(rs.getBigDecimal("monto_extra"));
        int idEsp = rs.getInt("id_especialidad");
        if (!rs.wasNull()) cp.setIdEspecialidad(idEsp);
        cp.setNombreEspecialidad(rs.getString("nombre_especialidad"));
        return cp;
    }

    private String traducirDiaSemana(int dayOfWeekValue) {
        return switch (dayOfWeekValue) {
            case 1 -> "LUNES";
            case 2 -> "MARTES";
            case 3 -> "MIERCOLES";
            case 4 -> "JUEVES";
            case 5 -> "VIERNES";
            case 6 -> "SABADO";
            case 7 -> "DOMINGO";
            default -> "LUNES";
        };
    }
}

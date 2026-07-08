package edu.upn.clinica.backend.consultorio.repository;

import edu.upn.clinica.backend.consultorio.model.Consultorio;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ConsultorioRepository extends BaseRepository {

    public List<Consultorio> findAll() {
        String sql = "SELECT id_consultorio, nombre, ubicacion, estado FROM consultorios ORDER BY nombre";
        List<Consultorio> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (Exception e) {
            throw new RuntimeException("Error listando consultorios: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Consultorio> findById(Integer id) {
        String sql = "SELECT id_consultorio, nombre, ubicacion, estado FROM consultorios WHERE id_consultorio = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando consultorio: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Consultorio save(Consultorio c) {
        String sql = "INSERT INTO consultorios (nombre, ubicacion, estado) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getUbicacion());
            ps.setString(3, c.getEstado() != null ? c.getEstado() : "ACTIVO");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdConsultorio(rs.getInt(1));
            }
            return c;
        } catch (Exception e) {
            throw new RuntimeException("Error creando consultorio: " + e.getMessage());
        }
    }

    public void update(Integer id, String nombre, String ubicacion) {
        String sql = "UPDATE consultorios SET nombre = ?, ubicacion = ? WHERE id_consultorio = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, ubicacion);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando consultorio: " + e.getMessage());
        }
    }

    public void updateEstado(Integer id, String estado) {
        String sql = "UPDATE consultorios SET estado = ? WHERE id_consultorio = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando estado consultorio: " + e.getMessage());
        }
    }

    public boolean existeConflicto(Integer idConsultorio, Integer idDoctor, String diaSemana,
                                    String horaInicio, String horaFin) {
        String sql = "SELECT COUNT(*) FROM doctor_consultorio "
                + "WHERE id_consultorio = ? AND id_doctor != ? AND dia_semana = ? "
                + "AND ((hora_inicio < ? AND hora_fin > ?) "
                + "  OR (hora_inicio < ? AND hora_fin > ?) "
                + "  OR (hora_inicio >= ? AND hora_fin <= ?))";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsultorio);
            ps.setInt(2, idDoctor != null ? idDoctor : 0);
            ps.setString(3, diaSemana);
            ps.setTime(4, Time.valueOf(horaFin));
            ps.setTime(5, Time.valueOf(horaInicio));
            ps.setTime(6, Time.valueOf(horaFin));
            ps.setTime(7, Time.valueOf(horaInicio));
            ps.setTime(8, Time.valueOf(horaInicio));
            ps.setTime(9, Time.valueOf(horaFin));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error verificando conflicto de consultorio: " + e.getMessage());
        }
    }

    public void asignarADoctor(Integer idConsultorio, Integer idDoctor, String diaSemana,
                                String horaInicio, String horaFin) {
        String sql = "INSERT INTO doctor_consultorio (id_doctor, id_consultorio, dia_semana, hora_inicio, hora_fin) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            ps.setInt(2, idConsultorio);
            ps.setString(3, diaSemana);
            ps.setTime(4, Time.valueOf(horaInicio));
            ps.setTime(5, Time.valueOf(horaFin));
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error asignando consultorio: " + e.getMessage());
        }
    }

    public List<Consultorio> findDisponiblesPorHorario(String diaSemana, String hora) {
        String sql = "SELECT c.id_consultorio, c.nombre, c.ubicacion, c.estado "
                + "FROM consultorios c "
                + "WHERE c.estado = 'ACTIVO' "
                + "AND c.id_consultorio NOT IN ( "
                + "  SELECT dc.id_consultorio FROM doctor_consultorio dc "
                + "  WHERE dc.dia_semana = ? AND ? BETWEEN dc.hora_inicio AND dc.hora_fin "
                + ") "
                + "ORDER BY c.nombre";
        List<Consultorio> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, diaSemana);
            ps.setTime(2, Time.valueOf(hora));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando consultorios disponibles: " + e.getMessage());
        }
        return lista;
    }

    private Consultorio mapRow(ResultSet rs) throws Exception {
        Consultorio c = new Consultorio();
        c.setIdConsultorio(rs.getInt("id_consultorio"));
        c.setNombre(rs.getString("nombre"));
        c.setUbicacion(rs.getString("ubicacion"));
        c.setEstado(rs.getString("estado"));
        return c;
    }

    public List<Map<String, Object>> findAllAsignaciones() {
        String sql = "SELECT dc.id_asignacion, dc.id_doctor, dc.id_consultorio, c.nombre AS consultorio, " +
                "CONCAT(u.nombre, ' ', u.apellido) AS doctor, dc.dia_semana, dc.hora_inicio, dc.hora_fin " +
                "FROM doctor_consultorio dc " +
                "JOIN consultorios c ON dc.id_consultorio = c.id_consultorio " +
                "JOIN doctores d ON dc.id_doctor = d.id_doctor " +
                "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
                "ORDER BY dc.dia_semana, dc.hora_inicio";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("idAsignacion", rs.getInt("id_asignacion"));
                    m.put("idDoctor", rs.getInt("id_doctor"));
                    m.put("idConsultorio", rs.getInt("id_consultorio"));
                    m.put("consultorio", rs.getString("consultorio"));
                    m.put("doctor", rs.getString("doctor"));
                    m.put("diaSemana", rs.getString("dia_semana"));
                    m.put("horaInicio", rs.getTime("hora_inicio").toString().substring(0,5));
                    m.put("horaFin", rs.getTime("hora_fin").toString().substring(0,5));
                    list.add(m);
                }
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    public void deleteAsignacion(Integer id) {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM doctor_consultorio WHERE id_asignacion = ?")) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    public Integer findConsultorioForDoctor(Integer idDoctor, String diaSemana, String hora) {
        String sql = "SELECT c.id_consultorio FROM consultorios c " +
                "JOIN doctor_consultorio dc ON c.id_consultorio = dc.id_consultorio AND dc.id_doctor = ? " +
                "AND dc.dia_semana = ? AND dc.hora_inicio <= ? AND dc.hora_fin > ? " +
                "WHERE c.estado = 'ACTIVO' " +
                "AND c.id_consultorio NOT IN (" +
                "  SELECT ct.id_consultorio FROM citas ct " +
                "  WHERE ct.fecha = CURDATE() AND ct.hora = ? AND ct.estado NOT IN ('CANCELADA','NO_ASISTIO')" +
                ") LIMIT 1";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            ps.setString(2, diaSemana);
            ps.setTime(3, java.sql.Time.valueOf(hora + ":00"));
            ps.setTime(4, java.sql.Time.valueOf(hora + ":00"));
            ps.setTime(5, java.sql.Time.valueOf(hora + ":00"));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_consultorio");
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return findDisponiblesPorHorario(diaSemana, hora).stream()
                .findFirst().map(Consultorio::getIdConsultorio).orElse(null);
    }

    public List<Integer> findDoctoresMedicinaGeneralActivos() {
        String sql = "SELECT d.id_doctor FROM doctores d " +
                "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
                "WHERE d.especialidad = 'Medicina General' AND u.estado = 'ACTIVO' " +
                "AND u.rol IN ('DOCTOR', 'MEDICO') " +
                "ORDER BY u.nombre";
        List<Integer> ids = new java.util.ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("id_doctor"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando doctores medicina general: " + e.getMessage());
        }
        return ids;
    }

    public List<Map<String, Object>> findOcupacion(String fecha) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT c.id_consultorio, c.nombre, c.ubicacion, ct.hora, ct.estado, " +
                "CONCAT(u.nombre,' ',u.apellido) AS doctor, CONCAT(up.nombre,' ',up.apellido) AS paciente " +
                "FROM consultorios c " +
                "LEFT JOIN citas ct ON ct.id_consultorio = c.id_consultorio AND ct.fecha = ? AND ct.estado IN ('CONFIRMADA','EN_ATENCION') " +
                "LEFT JOIN doctores d ON ct.id_doctor = d.id_doctor " +
                "LEFT JOIN usuarios u ON d.id_usuario = u.id_usuario " +
                "LEFT JOIN pacientes p ON ct.id_paciente = p.id_paciente " +
                "LEFT JOIN usuarios up ON p.id_usuario = up.id_usuario " +
                "WHERE c.estado = 'ACTIVO' " +
                "ORDER BY c.nombre, ct.hora";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("idConsultorio", rs.getInt("id_consultorio"));
                    m.put("nombre", rs.getString("nombre"));
                    m.put("ubicacion", rs.getString("ubicacion"));
                    m.put("hora", rs.getTime("hora") != null ? rs.getTime("hora").toString().substring(0,5) : null);
                    m.put("estado", rs.getString("estado"));
                    m.put("doctor", rs.getString("doctor"));
                    m.put("paciente", rs.getString("paciente"));
                    result.add(m);
                }
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return result;
    }
}

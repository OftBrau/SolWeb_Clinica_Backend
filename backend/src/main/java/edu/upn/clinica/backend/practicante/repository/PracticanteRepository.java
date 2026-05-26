package edu.upn.clinica.backend.practicante.repository;

import edu.upn.clinica.backend.practicante.model.ActividadPracticante;
import edu.upn.clinica.backend.practicante.model.EvaluacionPracticante;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PracticanteRepository extends BaseRepository {

    // ─── Actividades ──────────────────────────────────

    public List<ActividadPracticante> findAllActividades() {
        String sql = "SELECT * FROM actividades_practicante ORDER BY fecha DESC, hora DESC";
        List<ActividadPracticante> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapActividad(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando actividades: " + e.getMessage());
        }
        return lista;
    }

    public List<ActividadPracticante> findActividadesByPracticante(Integer idPracticante) {
        String sql = "SELECT * FROM actividades_practicante WHERE id_practicante = ? ORDER BY fecha DESC, hora DESC";
        List<ActividadPracticante> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPracticante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapActividad(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando actividades: " + e.getMessage());
        }
        return lista;
    }

    public Optional<ActividadPracticante> findActividadById(Integer id) {
        String sql = "SELECT * FROM actividades_practicante WHERE id_actividad = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapActividad(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando actividad: " + e.getMessage());
        }
        return Optional.empty();
    }

    // ─── Evaluaciones ─────────────────────────────────

    public List<EvaluacionPracticante> findEvaluacionesByPracticante(Integer idPracticante) {
        String sql = "SELECT * FROM evaluaciones_practicante WHERE id_practicante = ? ORDER BY fecha DESC";
        List<EvaluacionPracticante> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPracticante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapEvaluacion(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando evaluaciones: " + e.getMessage());
        }
        return lista;
    }

    // ─── Buscar ID de practicante por email ───────────

    public Optional<Integer> findIdByEmail(String email) {
        String sql = "SELECT p.id_doctor FROM doctores p " +
                     "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                     "WHERE u.email = ? AND u.rol = 'PRACTICANTE' AND u.estado = 'ACTIVO'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt(1));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando practicante: " + e.getMessage());
        }
        return Optional.empty();
    }

    // ─── Pacientes asignados ──────────────────────────

    public List<Object[]> findPacientesAsignados(Integer idPracticante, String query) {
        String sql = "SELECT DISTINCT p.id_paciente, CONCAT(u.nombre, ' ', u.apellido) AS nombre_completo, " +
                     "p.codigo_estudiante, " +
                     "(SELECT MAX(c.created_at) FROM consultas c WHERE c.id_paciente = p.id_paciente) AS ultima_consulta " +
                     "FROM pacientes p " +
                     "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                     "LEFT JOIN consultas c ON c.id_paciente = p.id_paciente " +
                     "WHERE (CONCAT(u.nombre, ' ', u.apellido) LIKE ? OR p.codigo_estudiante LIKE ?) " +
                     "GROUP BY p.id_paciente, u.nombre, u.apellido, p.codigo_estudiante " +
                     "ORDER BY u.nombre LIMIT 20";
        List<Object[]> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + query + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getInt("id_paciente"),
                        rs.getString("nombre_completo"),
                        rs.getString("codigo_estudiante"),
                        rs.getTimestamp("ultima_consulta") != null ?
                            rs.getTimestamp("ultima_consulta").toLocalDateTime().toString() : null
                    });
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando pacientes asignados: " + e.getMessage());
        }
        return lista;
    }

    // ─── Enviar consulta a revisión ────────────────────

    public void enviarARevision(Integer idConsulta) {
        String sql = "UPDATE consultas SET estado_revision = 'PENDIENTE_REVISION', updated_at = NOW() WHERE id_consulta = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error enviando a revisión: " + e.getMessage());
        }
    }

    // ─── Mappers ──────────────────────────────────────

    private ActividadPracticante mapActividad(ResultSet rs) throws Exception {
        ActividadPracticante a = new ActividadPracticante();
        a.setIdActividad(rs.getInt("id_actividad"));
        a.setIdPracticante(rs.getInt("id_practicante"));
        a.setTitulo(rs.getString("titulo"));
        a.setDescripcion(rs.getString("descripcion"));
        a.setTipo(rs.getString("tipo"));
        a.setFecha(rs.getDate("fecha").toLocalDate());
        Time h = rs.getTime("hora");
        if (h != null) a.setHora(h.toLocalTime());
        a.setEstado(rs.getString("estado"));
        int idP = rs.getInt("id_paciente");
        if (!rs.wasNull()) a.setIdPaciente(idP);
        int idS = rs.getInt("id_supervisor");
        if (!rs.wasNull()) a.setIdSupervisor(idS);
        return a;
    }

    private EvaluacionPracticante mapEvaluacion(ResultSet rs) throws Exception {
        EvaluacionPracticante e = new EvaluacionPracticante();
        e.setIdEvaluacion(rs.getInt("id_evaluacion"));
        e.setIdPracticante(rs.getInt("id_practicante"));
        e.setIdSupervisor(rs.getInt("id_supervisor"));
        e.setFecha(rs.getDate("fecha").toLocalDate());
        e.setPuntuacion(rs.getDouble("puntuacion"));
        e.setComentario(rs.getString("comentario"));
        return e;
    }
}

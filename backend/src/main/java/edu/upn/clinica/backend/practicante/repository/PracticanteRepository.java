package edu.upn.clinica.backend.practicante.repository;

import edu.upn.clinica.backend.practicante.model.ActividadPracticante;
import edu.upn.clinica.backend.practicante.model.EvaluacionPracticante;
import edu.upn.clinica.backend.practicante.model.PracticanteEntidad;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    // ─── Admin/Doctor: Asignación de practicantes ─────
    //     supervision_practicantes.id_practicante = doctores.id_doctor
    //     supervision_practicantes.id_supervisor  = doctores.id_doctor

    public List<PracticanteEntidad> findAllAsignaciones() {
        String sql =
            "SELECT sp.id_asignacion, sp.id_practicante, sp.id_supervisor, " +
            "       up.nombre AS nombre_usuario, up.apellido AS apellido_usuario, up.email AS email_usuario, " +
            "       us.nombre AS nombre_doctor, us.apellido AS apellido_doctor " +
            "FROM supervision_practicantes sp " +
            "JOIN doctores dp ON sp.id_practicante = dp.id_doctor " +
            "JOIN usuarios up ON dp.id_usuario = up.id_usuario " +
            "JOIN doctores ds ON sp.id_supervisor = ds.id_doctor " +
            "JOIN usuarios us ON ds.id_usuario = us.id_usuario " +
            "ORDER BY us.nombre, up.nombre";
        List<PracticanteEntidad> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapAsignacion(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando asignaciones: " + e.getMessage());
        }
        return lista;
    }

    public List<PracticanteEntidad> findAsignacionesByDoctor(Integer idDoctor) {
        String sql =
            "SELECT sp.id_asignacion, sp.id_practicante, sp.id_supervisor, " +
            "       up.nombre AS nombre_usuario, up.apellido AS apellido_usuario, up.email AS email_usuario, " +
            "       us.nombre AS nombre_doctor, us.apellido AS apellido_doctor " +
            "FROM supervision_practicantes sp " +
            "JOIN doctores dp ON sp.id_practicante = dp.id_doctor " +
            "JOIN usuarios up ON dp.id_usuario = up.id_usuario " +
            "JOIN doctores ds ON sp.id_supervisor = ds.id_doctor " +
            "JOIN usuarios us ON ds.id_usuario = us.id_usuario " +
            "WHERE sp.id_supervisor = ? " +
            "ORDER BY up.nombre";
        List<PracticanteEntidad> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapAsignacion(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando asignaciones por doctor: " + e.getMessage());
        }
        return lista;
    }

    /** Retorna doctores con rol PRACTICANTE que no tienen supervisor asignado */
    public List<Object[]> findPracticantesDisponibles() {
        String sql =
            "SELECT d.id_doctor, u.nombre, u.apellido, u.email " +
            "FROM doctores d " +
            "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
            "WHERE u.rol = 'PRACTICANTE' AND u.estado = 'ACTIVO' " +
            "  AND d.id_doctor NOT IN (SELECT id_practicante FROM supervision_practicantes) " +
            "ORDER BY u.nombre, u.apellido";
        List<Object[]> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getInt("id_doctor"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("email")
                    });
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando practicantes disponibles: " + e.getMessage());
        }
        return lista;
    }

    public void asignar(Integer idPracticante, Integer idSupervisor) {
        String sql = "INSERT INTO supervision_practicantes (id_practicante, id_supervisor) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPracticante);
            ps.setInt(2, idSupervisor);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error asignando practicante: " + e.getMessage());
        }
    }

    public void eliminarAsignacion(Integer idAsignacion) {
        String sql = "DELETE FROM supervision_practicantes WHERE id_asignacion = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAsignacion);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando asignación: " + e.getMessage());
        }
    }

    /** Obtener el supervisor (id_doctor) de un practicante */
    public Optional<Integer> findSupervisorByPracticante(Integer idPracticante) {
        String sql = "SELECT id_supervisor FROM supervision_practicantes WHERE id_practicante = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPracticante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt("id_supervisor"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando supervisor: " + e.getMessage());
        }
        return Optional.empty();
    }

    // ─── Doctor: Gestión de actividades para practicantes ──

    public ActividadPracticante crearActividad(ActividadPracticante a) {
        String sql = "INSERT INTO actividades_practicante (id_practicante, titulo, descripcion, tipo, fecha, hora, estado, id_paciente, id_supervisor, id_cita, id_teleconsulta) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getIdPracticante());
            ps.setString(2, a.getTitulo());
            ps.setString(3, a.getDescripcion());
            ps.setString(4, a.getTipo());
            ps.setDate(5, Date.valueOf(a.getFecha()));
            ps.setTime(6, a.getHora() != null ? Time.valueOf(a.getHora()) : null);
            ps.setString(7, a.getEstado());
            if (a.getIdPaciente() != null) ps.setInt(8, a.getIdPaciente());
            else ps.setNull(8, Types.INTEGER);
            if (a.getIdSupervisor() != null) ps.setInt(9, a.getIdSupervisor());
            else ps.setNull(9, Types.INTEGER);
            if (a.getIdCita() != null) ps.setInt(10, a.getIdCita());
            else ps.setNull(10, Types.INTEGER);
            if (a.getIdTeleconsulta() != null) ps.setInt(11, a.getIdTeleconsulta());
            else ps.setNull(11, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setIdActividad(rs.getInt(1));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creando actividad: " + e.getMessage());
        }
        return a;
    }

    /** Actividades creadas por un doctor supervisor para sus practicantes */
    public List<ActividadPracticante> findActividadesBySupervisor(Integer idSupervisor) {
        String sql = "SELECT * FROM actividades_practicante WHERE id_supervisor = ? ORDER BY fecha DESC, hora DESC";
        List<ActividadPracticante> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSupervisor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapActividad(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando actividades por supervisor: " + e.getMessage());
        }
        return lista;
    }

    /** Evaluaciones creadas por un doctor supervisor */
    public List<EvaluacionPracticante> findEvaluacionesBySupervisor(Integer idSupervisor) {
        String sql = "SELECT * FROM evaluaciones_practicante WHERE id_supervisor = ? ORDER BY fecha DESC";
        List<EvaluacionPracticante> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSupervisor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapEvaluacion(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando evaluaciones por supervisor: " + e.getMessage());
        }
        return lista;
    }

    /** Crear evaluación de un practicante (hecha por su supervisor) */
    public EvaluacionPracticante crearEvaluacion(EvaluacionPracticante e) {
        String sql = "INSERT INTO evaluaciones_practicante (id_practicante, id_supervisor, fecha, puntuacion, comentario) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getIdPracticante());
            ps.setInt(2, e.getIdSupervisor());
            ps.setDate(3, Date.valueOf(e.getFecha()));
            ps.setDouble(4, e.getPuntuacion());
            ps.setString(5, e.getComentario());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setIdEvaluacion(rs.getInt(1));
            }
        } catch (Exception e2) {
            throw new RuntimeException("Error creando evaluación: " + e2.getMessage());
        }
        return e;
    }

    // ─── Mappers ──────────────────────────────────────

    private PracticanteEntidad mapAsignacion(ResultSet rs) throws Exception {
        PracticanteEntidad p = new PracticanteEntidad();
        p.setIdAsignacion(rs.getInt("id_asignacion"));
        p.setIdPracticante(rs.getInt("id_practicante"));
        p.setIdSupervisor(rs.getInt("id_supervisor"));
        p.setNombreUsuario(rs.getString("nombre_usuario"));
        p.setApellidoUsuario(rs.getString("apellido_usuario"));
        p.setEmailUsuario(rs.getString("email_usuario"));
        p.setNombreDoctor(rs.getString("nombre_doctor"));
        p.setApellidoDoctor(rs.getString("apellido_doctor"));
        return p;
    }

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

    // --- Invitaciones ---
    public Map<String, Object> saveInvitacion(Integer idDoctor, Integer idPracticante, String mensaje) {
        String sql = "INSERT INTO invitaciones_practicante (id_doctor, id_practicante, mensaje) VALUES (?,?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idDoctor); ps.setInt(2, idPracticante); ps.setString(3, mensaje);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) return java.util.Map.of("id", keys.getInt(1)); }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return java.util.Map.of();
    }

    public List<java.util.Map<String, Object>> findInvitacionesByPracticante(Integer idPracticante) {
        List<java.util.Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT i.*, CONCAT(u.nombre,' ',u.apellido) AS doctor, d.especialidad FROM invitaciones_practicante i " +
                "JOIN doctores d ON i.id_doctor = d.id_doctor JOIN usuarios u ON d.id_usuario = u.id_usuario " +
                "WHERE i.id_practicante = ? ORDER BY i.fecha_creacion DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPracticante);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(mapInvitacion(rs)); }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    public List<java.util.Map<String, Object>> findInvitacionesByDoctor(Integer idDoctor) {
        List<java.util.Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT i.*, CONCAT(u.nombre,' ',u.apellido) AS practicante FROM invitaciones_practicante i " +
                "JOIN practicantes p ON i.id_practicante = p.id_practicante JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "WHERE i.id_doctor = ? ORDER BY i.fecha_creacion DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(mapInvitacion(rs)); }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    public void updateInvitacionEstado(Integer id, String estado) {
        String sql = "UPDATE invitaciones_practicante SET estado = ?, fecha_respuesta = CURRENT_TIMESTAMP WHERE id_invitacion = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado); ps.setInt(2, id); ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    public java.util.Map<String, Object> findInvitacionById(Integer id) {
        String sql = "SELECT i.*, CONCAT(du.nombre,' ',du.apellido) AS doctor, " +
                "CONCAT(pu.nombre,' ',pu.apellido) AS practicante, d.especialidad " +
                "FROM invitaciones_practicante i " +
                "JOIN doctores d ON i.id_doctor = d.id_doctor JOIN usuarios du ON d.id_usuario = du.id_usuario " +
                "JOIN practicantes p ON i.id_practicante = p.id_practicante JOIN usuarios pu ON p.id_usuario = pu.id_usuario " +
                "WHERE i.id_invitacion = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapInvitacion(rs); }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return null;
    }

    public Integer findIdPracticanteByDoctorId(Integer idDoctor) {
        String sql = "SELECT p.id_practicante FROM practicantes p JOIN doctores d ON p.id_usuario = d.id_usuario WHERE d.id_doctor = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt("id_practicante"); }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return null;
    }

    public void asignarSupervision(Integer idDoctor, Integer idPracticante) {
        String sql = "INSERT IGNORE INTO supervision_practicantes (id_supervisor, id_practicante) VALUES (?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idDoctor); ps.setInt(2, idPracticante); ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    private java.util.Map<String, Object> mapInvitacion(ResultSet rs) throws Exception {
        java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("idInvitacion", rs.getInt("id_invitacion"));
        m.put("idDoctor", rs.getInt("id_doctor"));
        m.put("idPracticante", rs.getInt("id_practicante"));
        m.put("mensaje", rs.getString("mensaje"));
        m.put("estado", rs.getString("estado"));
        Timestamp fc = rs.getTimestamp("fecha_creacion");
        if (fc != null) m.put("fechaCreacion", fc.toLocalDateTime().toString());
        Timestamp fr = rs.getTimestamp("fecha_respuesta");
        if (fr != null) m.put("fechaRespuesta", fr.toLocalDateTime().toString());
        try { m.put("doctor", rs.getString("doctor")); } catch (Exception e) {}
        try { m.put("practicante", rs.getString("practicante")); } catch (Exception e) {}
        try { m.put("especialidad", rs.getString("especialidad")); } catch (Exception e) {}
        return m;
    }

    // --- Tareas ---
    public Map<String, Object> saveTarea(Integer idDoctor, Integer idPracticante, String titulo, String descripcion, String tipo, String prioridad, String fechaLimite) {
        String sql = "INSERT INTO tareas_practicante (id_doctor, id_practicante, titulo, descripcion, tipo, prioridad, fecha_limite) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idDoctor); ps.setInt(2, idPracticante); ps.setString(3, titulo);
            ps.setString(4, descripcion); ps.setString(5, tipo != null ? tipo : "OTRO");
            ps.setString(6, prioridad != null ? prioridad : "MEDIA");
            ps.setDate(7, fechaLimite != null && !fechaLimite.isBlank() ? Date.valueOf(fechaLimite) : null);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) return Map.of("id", keys.getInt(1)); }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return Map.of();
    }

    public List<Map<String, Object>> findTareasByPracticante(Integer idPracticante) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT t.*, CONCAT(u.nombre,' ',u.apellido) AS doctor FROM tareas_practicante t " +
                "JOIN doctores d ON t.id_doctor = d.id_doctor JOIN usuarios u ON d.id_usuario = u.id_usuario " +
                "WHERE t.id_practicante = ? ORDER BY t.fecha_asignacion DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPracticante);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(mapTarea(rs)); }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    public List<Map<String, Object>> findTareasByDoctor(Integer idDoctor) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT t.*, CONCAT(u.nombre,' ',u.apellido) AS practicante FROM tareas_practicante t " +
                "JOIN practicantes p ON t.id_practicante = p.id_practicante JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "WHERE t.id_doctor = ? ORDER BY t.fecha_asignacion DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(mapTarea(rs)); }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    public void updateTareaEstado(Integer id, String estado) {
        String sql = "UPDATE tareas_practicante SET estado = ? WHERE id_tarea = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado); ps.setInt(2, id); ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    private Map<String, Object> mapTarea(ResultSet rs) throws Exception {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idTarea", rs.getInt("id_tarea"));
        m.put("idDoctor", rs.getInt("id_doctor"));
        m.put("idPracticante", rs.getInt("id_practicante"));
        m.put("titulo", rs.getString("titulo"));
        m.put("descripcion", rs.getString("descripcion"));
        m.put("tipo", rs.getString("tipo"));
        m.put("prioridad", rs.getString("prioridad"));
        m.put("estado", rs.getString("estado"));
        Timestamp fa = rs.getTimestamp("fecha_asignacion");
        if (fa != null) m.put("fechaAsignacion", fa.toLocalDateTime().toString());
        Date fl = rs.getDate("fecha_limite");
        if (fl != null) m.put("fecha_limite", fl.toString());
        try { m.put("doctor", rs.getString("doctor")); } catch (Exception ex) {}
        try { m.put("practicante", rs.getString("practicante")); } catch (Exception ex) {}
        return m;
    }
}

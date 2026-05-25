package edu.upn.clinica.backend.log.repository;

import edu.upn.clinica.backend.log.model.LogActividad;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LogRepository extends BaseRepository {

    public void save(LogActividad log) {
        String sql = "INSERT INTO logs_actividad (id_usuario, email, accion, detalle, ip) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            if (log.getIdUsuario() != null) ps.setInt(1, log.getIdUsuario());
            else ps.setNull(1, Types.INTEGER);
            ps.setString(2, log.getEmail());
            ps.setString(3, log.getAccion());
            ps.setString(4, log.getDetalle());
            ps.setString(5, log.getIp());
            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error guardando log: " + e.getMessage());
        }
    }

    public List<LogActividad> findAll(int page, int size) {
        String sql = "SELECT id_log, id_usuario, email, accion, detalle, ip, created_at " +
                     "FROM logs_actividad ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return query(sql, size, page * size);
    }

    public List<LogActividad> findByEmail(String email, int page, int size) {
        String sql = "SELECT id_log, id_usuario, email, accion, detalle, ip, created_at " +
                     "FROM logs_actividad WHERE email = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return query(sql, email, size, page * size);
    }

    public List<LogActividad> findByAccion(String accion, int page, int size) {
        String sql = "SELECT id_log, id_usuario, email, accion, detalle, ip, created_at " +
                     "FROM logs_actividad WHERE accion LIKE ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        return query(sql, "%" + accion + "%", size, page * size);
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM logs_actividad";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando logs: " + e.getMessage());
        }
    }

    private List<LogActividad> query(String sql, Object... params) {
        List<LogActividad> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando logs: " + e.getMessage());
        }
        return lista;
    }

    private LogActividad mapRow(ResultSet rs) throws Exception {
        LogActividad l = new LogActividad();
        l.setIdLog(rs.getInt("id_log"));
        int uid = rs.getInt("id_usuario");
        if (!rs.wasNull()) l.setIdUsuario(uid);
        l.setEmail(rs.getString("email"));
        l.setAccion(rs.getString("accion"));
        l.setDetalle(rs.getString("detalle"));
        l.setIp(rs.getString("ip"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) l.setCreatedAt(ts.toLocalDateTime());
        return l;
    }
}

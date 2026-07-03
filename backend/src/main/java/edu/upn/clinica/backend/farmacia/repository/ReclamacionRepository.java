package edu.upn.clinica.backend.farmacia.repository;

import edu.upn.clinica.backend.farmacia.model.Reclamacion;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ReclamacionRepository extends BaseRepository {

    private static final String SELECT_BASE
            = "SELECT id_reclamacion, id_paciente, nombre_completo, email, telefono, "
            + "tipo, descripcion, producto_servicio, estado, respuesta, "
            + "fecha_creacion, fecha_respuesta FROM reclamaciones ";

    public List<Reclamacion> findAll(int page, int size) {
        String sql = SELECT_BASE + "ORDER BY fecha_creacion DESC LIMIT ? OFFSET ?";
        List<Reclamacion> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, size);
            ps.setInt(2, page * size);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando reclamaciones: " + e.getMessage());
        }
        return lista;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM reclamaciones";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando reclamaciones: " + e.getMessage());
        }
    }

    public Optional<Reclamacion> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE id_reclamacion = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando reclamacion: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Reclamacion> findByPaciente(Integer idPaciente) {
        String sql = SELECT_BASE + "WHERE id_paciente = ? ORDER BY fecha_creacion DESC";
        List<Reclamacion> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando reclamaciones: " + e.getMessage());
        }
        return lista;
    }

    public Reclamacion save(Reclamacion r) {
        String sql = "INSERT INTO reclamaciones (id_paciente, nombre_completo, email, telefono, "
                + "tipo, descripcion, producto_servicio) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (r.getIdPaciente() != null) ps.setInt(1, r.getIdPaciente());
            else ps.setNull(1, Types.INTEGER);
            ps.setString(2, r.getNombreCompleto());
            ps.setString(3, r.getEmail());
            ps.setString(4, r.getTelefono());
            ps.setString(5, r.getTipo());
            ps.setString(6, r.getDescripcion());
            ps.setString(7, r.getProductoServicio());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setIdReclamacion(keys.getInt(1));
            }
            return r;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando reclamacion: " + e.getMessage());
        }
    }

    public void updateEstado(Integer id, String estado, String respuesta) {
        String sql = "UPDATE reclamaciones SET estado = ?, respuesta = ?, "
                + "fecha_respuesta = CURRENT_TIMESTAMP WHERE id_reclamacion = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setString(2, respuesta);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando reclamacion: " + e.getMessage());
        }
    }

    private Reclamacion mapRow(ResultSet rs) throws Exception {
        Reclamacion r = new Reclamacion();
        r.setIdReclamacion(rs.getInt("id_reclamacion"));
        r.setIdPaciente(rs.getObject("id_paciente") != null ? rs.getInt("id_paciente") : null);
        r.setNombreCompleto(rs.getString("nombre_completo"));
        r.setEmail(rs.getString("email"));
        r.setTelefono(rs.getString("telefono"));
        r.setTipo(rs.getString("tipo"));
        r.setDescripcion(rs.getString("descripcion"));
        r.setProductoServicio(rs.getString("producto_servicio"));
        r.setEstado(rs.getString("estado"));
        r.setRespuesta(rs.getString("respuesta"));
        Timestamp fc = rs.getTimestamp("fecha_creacion");
        if (fc != null) r.setFechaCreacion(fc.toLocalDateTime());
        Timestamp fr = rs.getTimestamp("fecha_respuesta");
        if (fr != null) r.setFechaRespuesta(fr.toLocalDateTime());
        return r;
    }
}

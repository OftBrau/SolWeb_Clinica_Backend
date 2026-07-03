package edu.upn.clinica.backend.farmacia.repository;

import edu.upn.clinica.backend.farmacia.model.DetalleVenta;
import edu.upn.clinica.backend.farmacia.model.Venta;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VentaRepository extends BaseRepository {

    public Venta save(Venta v) {
        String sql = "INSERT INTO ventas_farmacia (id_paciente, total, estado, metodo_pago, id_preferencia_mp) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, v.getIdPaciente());
            ps.setBigDecimal(2, v.getTotal());
            ps.setString(3, v.getEstado());
            ps.setString(4, v.getMetodoPago());
            ps.setString(5, v.getIdPreferenciaMp());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) v.setIdVenta(keys.getInt(1));
            }
            return v;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando venta: " + e.getMessage());
        }
    }

    public void saveDetalle(DetalleVenta d) {
        String sql = "INSERT INTO detalle_venta_farmacia (id_venta, id_medicamento, cantidad, "
                + "precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getIdVenta());
            ps.setInt(2, d.getIdMedicamento());
            ps.setInt(3, d.getCantidad());
            ps.setBigDecimal(4, d.getPrecioUnitario());
            ps.setBigDecimal(5, d.getSubtotal());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) d.setIdDetalle(keys.getInt(1));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error guardando detalle venta: " + e.getMessage());
        }
    }

    public Optional<Venta> findById(Integer id) {
        String sql = "SELECT v.*, u.nombre AS nombre_paciente, u.apellido AS apellido_paciente "
                + "FROM ventas_farmacia v "
                + "JOIN pacientes p ON v.id_paciente = p.id_paciente "
                + "JOIN usuarios u ON p.id_usuario = u.id_usuario "
                + "WHERE v.id_venta = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Venta v = mapRow(rs);
                    v.setDetalles(findDetallesByVenta(id));
                    return Optional.of(v);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando venta: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Venta> findByPaciente(Integer idPaciente) {
        String sql = "SELECT * FROM ventas_farmacia WHERE id_paciente = ? ORDER BY fecha_venta DESC";
        List<Venta> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando ventas del paciente: " + e.getMessage());
        }
        return lista;
    }

    public List<DetalleVenta> findDetallesByVenta(Integer idVenta) {
        String sql = "SELECT d.*, m.nombre_comercial FROM detalle_venta_farmacia d "
                + "JOIN medicamentos m ON d.id_medicamento = m.id_medicamento "
                + "WHERE d.id_venta = ?";
        List<DetalleVenta> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta d = new DetalleVenta();
                    d.setIdDetalle(rs.getInt("id_detalle"));
                    d.setIdVenta(rs.getInt("id_venta"));
                    d.setIdMedicamento(rs.getInt("id_medicamento"));
                    d.setCantidad(rs.getInt("cantidad"));
                    d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    d.setSubtotal(rs.getBigDecimal("subtotal"));
                    d.setNombreComercial(rs.getString("nombre_comercial"));
                    lista.add(d);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando detalles de venta: " + e.getMessage());
        }
        return lista;
    }

    public Venta savePublic(String nombre, String email, String telefono, BigDecimal total, String estado, String metodoPago) {
        String sql = "INSERT INTO ventas_farmacia (nombre_contacto, email_contacto, telefono_contacto, total, estado, metodo_pago) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, email);
            ps.setString(3, telefono);
            ps.setBigDecimal(4, total);
            ps.setString(5, estado);
            ps.setString(6, metodoPago);
            ps.executeUpdate();
            Venta v = new Venta();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) v.setIdVenta(keys.getInt(1));
            }
            v.setTotal(total);
            v.setEstado(estado);
            v.setMetodoPago(metodoPago);
            return v;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando venta: " + e.getMessage());
        }
    }

    public void updateEstado(Integer id, String estado, String idPagoMp) {
        String sql = "UPDATE ventas_farmacia SET estado = ?, id_pago_mp = ? WHERE id_venta = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setString(2, idPagoMp);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando estado venta: " + e.getMessage());
        }
    }

    public void updatePreferenciaMp(Integer idVenta, String idPreferenciaMp) {
        String sql = "UPDATE ventas_farmacia SET id_preferencia_mp = ? WHERE id_venta = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idPreferenciaMp);
            ps.setInt(2, idVenta);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando preferencia MP: " + e.getMessage());
        }
    }

    public List<Venta> findAll(int page, int size) {
        String sql = "SELECT * FROM ventas_farmacia ORDER BY fecha_venta DESC LIMIT ? OFFSET ?";
        List<Venta> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, size);
            ps.setInt(2, page * size);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando ventas: " + e.getMessage());
        }
        return lista;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM ventas_farmacia";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando ventas: " + e.getMessage());
        }
    }

    public Optional<Venta> findByPreferenciaMp(String idPreferenciaMp) {
        String sql = "SELECT * FROM ventas_farmacia WHERE id_preferencia_mp = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idPreferenciaMp);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando venta por preferencia: " + e.getMessage());
        }
        return Optional.empty();
    }

    private Venta mapRow(ResultSet rs) throws Exception {
        Venta v = new Venta();
        v.setIdVenta(rs.getInt("id_venta"));
        v.setIdPaciente(rs.getInt("id_paciente"));
        v.setTotal(rs.getBigDecimal("total"));
        v.setEstado(rs.getString("estado"));
        v.setMetodoPago(rs.getString("metodo_pago"));
        v.setIdPreferenciaMp(rs.getString("id_preferencia_mp"));
        v.setIdPagoMp(rs.getString("id_pago_mp"));
        Timestamp ts = rs.getTimestamp("fecha_venta");
        if (ts != null) v.setFechaVenta(ts.toLocalDateTime());
        return v;
    }
}

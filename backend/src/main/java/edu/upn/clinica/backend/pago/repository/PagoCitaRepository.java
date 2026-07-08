package edu.upn.clinica.backend.pago.repository;

import edu.upn.clinica.backend.pago.model.PagoCita;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PagoCitaRepository extends BaseRepository {

    public PagoCita save(PagoCita pago) {
        String sql = "INSERT INTO pagos_citas (id_cita, monto, metodo_pago, estado_pago, referencia_mp) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pago.getIdCita());
            ps.setBigDecimal(2, pago.getMonto());
            ps.setString(3, pago.getMetodoPago() != null ? pago.getMetodoPago() : "MERCADOPAGO");
            ps.setString(4, pago.getEstadoPago() != null ? pago.getEstadoPago() : "PENDIENTE");
            ps.setString(5, pago.getReferenciaMp());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) pago.setIdPago(rs.getInt(1));
            }
            return pago;
        } catch (Exception e) {
            throw new RuntimeException("Error creando pago: " + e.getMessage());
        }
    }

    public Optional<PagoCita> findByCita(Integer idCita) {
        String sql = "SELECT id_pago, id_cita, monto, metodo_pago, estado_pago, referencia_mp, fecha_pago " +
                "FROM pagos_citas WHERE id_cita = ? ORDER BY fecha_pago DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando pago: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void updateEstado(Integer idPago, String estadoPago, String referenciaMp) {
        String sql = "UPDATE pagos_citas SET estado_pago = ?, referencia_mp = ? WHERE id_pago = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estadoPago);
            ps.setString(2, referenciaMp);
            ps.setInt(3, idPago);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando pago: " + e.getMessage());
        }
    }

    public List<PagoCita> findByPaciente(Integer idPaciente) {
        String sql = "SELECT pc.id_pago, pc.id_cita, pc.monto, pc.metodo_pago, pc.estado_pago, " +
                "pc.referencia_mp, pc.fecha_pago " +
                "FROM pagos_citas pc " +
                "JOIN citas c ON pc.id_cita = c.id_cita " +
                "WHERE c.id_paciente = ? " +
                "ORDER BY pc.fecha_pago DESC";
        List<PagoCita> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando pagos: " + e.getMessage());
        }
        return lista;
    }

    private PagoCita mapRow(ResultSet rs) throws Exception {
        PagoCita p = new PagoCita();
        p.setIdPago(rs.getInt("id_pago"));
        p.setIdCita(rs.getInt("id_cita"));
        p.setMonto(rs.getBigDecimal("monto"));
        p.setMetodoPago(rs.getString("metodo_pago"));
        p.setEstadoPago(rs.getString("estado_pago"));
        p.setReferenciaMp(rs.getString("referencia_mp"));
        Timestamp fp = rs.getTimestamp("fecha_pago");
        if (fp != null) p.setFechaPago(fp.toLocalDateTime());
        return p;
    }
}

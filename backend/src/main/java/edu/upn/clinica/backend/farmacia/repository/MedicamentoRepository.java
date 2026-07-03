package edu.upn.clinica.backend.farmacia.repository;

import edu.upn.clinica.backend.farmacia.model.Medicamento;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MedicamentoRepository extends BaseRepository {

    private static final String SELECT_BASE
            = "SELECT id_medicamento, nombre_comercial, nombre_generico, presentacion, "
            + "concentracion, laboratorio, stock, precio_unitario, requiere_receta, "
            + "descripcion, foto_url, fecha_vencimiento, activo, categoria FROM medicamentos ";

    public List<Medicamento> findAll(int page, int size) {
        String sql = SELECT_BASE + "ORDER BY nombre_comercial LIMIT ? OFFSET ?";
        List<Medicamento> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, size);
            ps.setInt(2, page * size);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando medicamentos: " + e.getMessage());
        }
        return lista;
    }

    public List<Medicamento> findAllActivos() {
        String sql = SELECT_BASE + "WHERE activo = TRUE AND stock > 0 ORDER BY nombre_comercial";
        List<Medicamento> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando medicamentos activos: " + e.getMessage());
        }
        return lista;
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM medicamentos";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando medicamentos: " + e.getMessage());
        }
    }

    public Optional<Medicamento> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE id_medicamento = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando medicamento: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Medicamento save(Medicamento m) {
        String sql = "INSERT INTO medicamentos (nombre_comercial, nombre_generico, presentacion, "
                + "concentracion, laboratorio, stock, precio_unitario, requiere_receta, "
                + "descripcion, foto_url, fecha_vencimiento, categoria) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getNombreComercial());
            ps.setString(2, m.getNombreGenerico());
            ps.setString(3, m.getPresentacion());
            ps.setString(4, m.getConcentracion());
            ps.setString(5, m.getLaboratorio());
            ps.setInt(6, m.getStock() != null ? m.getStock() : 0);
            ps.setBigDecimal(7, m.getPrecioUnitario());
            ps.setBoolean(8, m.getRequiereReceta() != null ? m.getRequiereReceta() : false);
            ps.setString(9, m.getDescripcion());
            ps.setString(10, m.getFotoUrl());
            ps.setDate(11, m.getFechaVencimiento() != null ? Date.valueOf(m.getFechaVencimiento()) : null);
            ps.setString(12, m.getCategoria() != null ? m.getCategoria() : "MEDICAMENTO");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) m.setIdMedicamento(keys.getInt(1));
            }
            return m;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando medicamento: " + e.getMessage());
        }
    }

    public void update(Medicamento m) {
        String sql = "UPDATE medicamentos SET nombre_comercial=?, nombre_generico=?, presentacion=?, "
                + "concentracion=?, laboratorio=?, stock=?, precio_unitario=?, requiere_receta=?, "
                + "descripcion=?, foto_url=?, fecha_vencimiento=?, categoria=? WHERE id_medicamento=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getNombreComercial());
            ps.setString(2, m.getNombreGenerico());
            ps.setString(3, m.getPresentacion());
            ps.setString(4, m.getConcentracion());
            ps.setString(5, m.getLaboratorio());
            ps.setInt(6, m.getStock());
            ps.setBigDecimal(7, m.getPrecioUnitario());
            ps.setBoolean(8, m.getRequiereReceta());
            ps.setString(9, m.getDescripcion());
            ps.setString(10, m.getFotoUrl());
            ps.setDate(11, m.getFechaVencimiento() != null ? Date.valueOf(m.getFechaVencimiento()) : null);
            ps.setString(12, m.getCategoria());
            ps.setInt(13, m.getIdMedicamento());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando medicamento: " + e.getMessage());
        }
    }

    public void deactivate(Integer id) {
        String sql = "UPDATE medicamentos SET activo = FALSE WHERE id_medicamento = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error desactivando medicamento: " + e.getMessage());
        }
    }

    public void updateStock(Integer id, int cantidad) {
        String sql = "UPDATE medicamentos SET stock = stock + ? WHERE id_medicamento = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando stock: " + e.getMessage());
        }
    }

    public void updateFoto(Integer id, String url) {
        String sql = "UPDATE medicamentos SET foto_url = ? WHERE id_medicamento = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, url);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando foto: " + e.getMessage());
        }
    }

    private Medicamento mapRow(ResultSet rs) throws Exception {
        Medicamento m = new Medicamento();
        m.setIdMedicamento(rs.getInt("id_medicamento"));
        m.setNombreComercial(rs.getString("nombre_comercial"));
        m.setNombreGenerico(rs.getString("nombre_generico"));
        m.setPresentacion(rs.getString("presentacion"));
        m.setConcentracion(rs.getString("concentracion"));
        m.setLaboratorio(rs.getString("laboratorio"));
        m.setStock(rs.getInt("stock"));
        m.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        m.setRequiereReceta(rs.getBoolean("requiere_receta"));
        m.setDescripcion(rs.getString("descripcion"));
        m.setFotoUrl(rs.getString("foto_url"));
        Date fv = rs.getDate("fecha_vencimiento");
        if (fv != null) m.setFechaVencimiento(fv.toLocalDate());
        m.setActivo(rs.getBoolean("activo"));
        m.setCategoria(rs.getString("categoria"));
        return m;
    }
}

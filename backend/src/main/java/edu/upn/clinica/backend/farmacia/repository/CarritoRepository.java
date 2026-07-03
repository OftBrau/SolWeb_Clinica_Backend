package edu.upn.clinica.backend.farmacia.repository;

import edu.upn.clinica.backend.farmacia.model.CarritoItem;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CarritoRepository extends BaseRepository {

    private static final String SELECT_BASE
            = "SELECT c.id_carrito, c.id_paciente, c.id_medicamento, c.cantidad, "
            + "m.nombre_comercial, m.precio_unitario, m.categoria "
            + "FROM carrito c "
            + "JOIN medicamentos m ON c.id_medicamento = m.id_medicamento ";

    public List<CarritoItem> findByPaciente(Integer idPaciente) {
        String sql = SELECT_BASE + "WHERE c.id_paciente = ? ORDER BY c.id_carrito";
        List<CarritoItem> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando carrito: " + e.getMessage());
        }
        return lista;
    }

    public Optional<CarritoItem> findByPacienteAndMedicamento(Integer idPaciente, Integer idMedicamento) {
        String sql = SELECT_BASE + "WHERE c.id_paciente = ? AND c.id_medicamento = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            ps.setInt(2, idMedicamento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando item carrito: " + e.getMessage());
        }
        return Optional.empty();
    }

    public CarritoItem addOrUpdate(Integer idPaciente, Integer idMedicamento, int cantidad) {
        Optional<CarritoItem> existente = findByPacienteAndMedicamento(idPaciente, idMedicamento);
        if (existente.isPresent()) {
            String sql = "UPDATE carrito SET cantidad = cantidad + ? WHERE id_carrito = ?";
            try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, cantidad);
                ps.setInt(2, existente.get().getIdCarrito());
                ps.executeUpdate();
            } catch (Exception e) {
                throw new RuntimeException("Error actualizando carrito: " + e.getMessage());
            }
            return findByPacienteAndMedicamento(idPaciente, idMedicamento).orElse(null);
        } else {
            String sql = "INSERT INTO carrito (id_paciente, id_medicamento, cantidad) VALUES (?, ?, ?)";
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, idPaciente);
                ps.setInt(2, idMedicamento);
                ps.setInt(3, cantidad);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        CarritoItem item = new CarritoItem();
                        item.setIdCarrito(keys.getInt(1));
                        item.setIdPaciente(idPaciente);
                        item.setIdMedicamento(idMedicamento);
                        item.setCantidad(cantidad);
                        return item;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error agregando al carrito: " + e.getMessage());
            }
        }
        return null;
    }

    public void updateCantidad(Integer idCarrito, int cantidad) {
        String sql = "UPDATE carrito SET cantidad = ? WHERE id_carrito = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idCarrito);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando cantidad: " + e.getMessage());
        }
    }

    public void remove(Integer idCarrito) {
        String sql = "DELETE FROM carrito WHERE id_carrito = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCarrito);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando item carrito: " + e.getMessage());
        }
    }

    public void clearByPaciente(Integer idPaciente) {
        String sql = "DELETE FROM carrito WHERE id_paciente = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error limpiando carrito: " + e.getMessage());
        }
    }

    public int countByPaciente(Integer idPaciente) {
        String sql = "SELECT COUNT(*) FROM carrito WHERE id_paciente = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error contando carrito: " + e.getMessage());
        }
    }

    private CarritoItem mapRow(ResultSet rs) throws Exception {
        CarritoItem item = new CarritoItem();
        item.setIdCarrito(rs.getInt("id_carrito"));
        item.setIdPaciente(rs.getInt("id_paciente"));
        item.setIdMedicamento(rs.getInt("id_medicamento"));
        item.setCantidad(rs.getInt("cantidad"));
        item.setNombreComercial(rs.getString("nombre_comercial"));
        item.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        item.setCategoria(rs.getString("categoria"));
        return item;
    }
}

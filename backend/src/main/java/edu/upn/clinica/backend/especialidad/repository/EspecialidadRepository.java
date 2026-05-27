package edu.upn.clinica.backend.especialidad.repository;

import edu.upn.clinica.backend.especialidad.model.Especialidad;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EspecialidadRepository extends BaseRepository {

    public List<Especialidad> findAll() {
        String sql = "SELECT id_especialidad, nombre, descripcion, estado FROM especialidades ORDER BY nombre";
        List<Especialidad> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (Exception e) {
            throw new RuntimeException("Error listando especialidades: " + e.getMessage());
        }
        return lista;
    }

    public List<Especialidad> findAllActivas() {
        String sql = "SELECT id_especialidad, nombre, descripcion, estado FROM especialidades WHERE estado = 'ACTIVO' ORDER BY nombre";
        List<Especialidad> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (Exception e) {
            throw new RuntimeException("Error listando especialidades: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Especialidad> findById(Integer id) {
        String sql = "SELECT id_especialidad, nombre, descripcion, estado FROM especialidades WHERE id_especialidad = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando especialidad: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean existsByNombre(String nombre) {
        String sql = "SELECT COUNT(*) FROM especialidades WHERE nombre = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error verificando especialidad: " + e.getMessage());
        }
    }

    public Especialidad save(Especialidad e) {
        String sql = "INSERT INTO especialidades (nombre, descripcion, estado) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getDescripcion());
            ps.setString(3, e.getEstado() != null ? e.getEstado() : "ACTIVO");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setIdEspecialidad(rs.getInt(1));
            }
            return e;
        } catch (Exception e2) {
            throw new RuntimeException("Error creando especialidad: " + e2.getMessage());
        }
    }

    public void update(Integer id, String nombre, String descripcion) {
        String sql = "UPDATE especialidades SET nombre = ?, descripcion = ? WHERE id_especialidad = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando especialidad: " + e.getMessage());
        }
    }

    public void updateEstado(Integer id, String estado) {
        String sql = "UPDATE especialidades SET estado = ? WHERE id_especialidad = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando estado especialidad: " + e.getMessage());
        }
    }

    private Especialidad mapRow(ResultSet rs) throws Exception {
        Especialidad e = new Especialidad();
        e.setIdEspecialidad(rs.getInt("id_especialidad"));
        e.setNombre(rs.getString("nombre"));
        e.setDescripcion(rs.getString("descripcion"));
        e.setEstado(rs.getString("estado"));
        return e;
    }
}

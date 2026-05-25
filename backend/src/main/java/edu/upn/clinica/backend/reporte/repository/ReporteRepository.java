package edu.upn.clinica.backend.reporte.repository;

import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReporteRepository extends BaseRepository {

    public long contarCitasPorEstado(String estado, String fecha) {
        String sql = "SELECT COUNT(*) FROM citas WHERE estado = ? AND fecha = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setDate(2, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error contando citas: " + e.getMessage());
        }
    }

    public long contarTotalCitas(String fecha) {
        String sql = "SELECT COUNT(*) FROM citas WHERE fecha = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error contando citas: " + e.getMessage());
        }
    }

    public long contarPacientesAtendidos(String fecha) {
        String sql = "SELECT COUNT(DISTINCT id_paciente) FROM consultas WHERE DATE(created_at) = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error contando pacientes: " + e.getMessage());
        }
    }

    public long contarDoctoresActivos() {
        String sql = "SELECT COUNT(*) FROM doctores d JOIN usuarios u ON d.id_usuario = u.id_usuario WHERE u.estado = 'ACTIVO'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando doctores: " + e.getMessage());
        }
    }

    public List<Object[]> contarCitasPorEspecialidad(String fecha) {
        String sql = "SELECT d.especialidad, COUNT(*) AS cantidad " +
                     "FROM citas c JOIN doctores d ON c.id_doctor = d.id_doctor " +
                     "WHERE c.fecha = ? GROUP BY d.especialidad ORDER BY cantidad DESC";
        List<Object[]> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{rs.getString("especialidad"), rs.getLong("cantidad")});
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error contando citas por especialidad: " + e.getMessage());
        }
        return lista;
    }

    public List<Object[]> contarCitasPorDoctor(String fecha) {
        String sql = "SELECT CONCAT(u.nombre, ' ', u.apellido) AS nombre_doctor, d.especialidad, COUNT(*) AS cantidad " +
                     "FROM citas c " +
                     "JOIN doctores d ON c.id_doctor = d.id_doctor " +
                     "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
                     "WHERE c.fecha = ? " +
                     "GROUP BY c.id_doctor, u.nombre, u.apellido, d.especialidad ORDER BY cantidad DESC";
        List<Object[]> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Object[]{
                        rs.getString("nombre_doctor"),
                        rs.getString("especialidad"),
                        rs.getLong("cantidad")
                    });
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error contando citas por doctor: " + e.getMessage());
        }
        return lista;
    }
}

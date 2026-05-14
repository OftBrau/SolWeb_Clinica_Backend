package edu.upn.clinica.backend.doctor.repository;

import edu.upn.clinica.backend.doctor.dto.DoctorDisponibleDTO;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// ============================================================
//  DoctorRepository.java
//  SQL puro con JDBC — tablas: doctores + usuarios
// ============================================================
@Repository
public class DoctorRepository extends BaseRepository {

    // --- Buscar doctores activos por especialidad ---
    // Usado por el formulario público de citas
    public List<DoctorDisponibleDTO> findByEspecialidad(String especialidad) {
        String sql =
            "SELECT d.id_doctor, " +
            "       CONCAT(u.nombre, ' ', u.apellido) AS nombre_completo, " +
            "       d.especialidad " +
            "FROM doctores d " +
            "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
            "WHERE d.especialidad = ? " +
            "  AND u.estado = 'ACTIVO' " +
            "ORDER BY u.nombre";

        List<DoctorDisponibleDTO> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, especialidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new DoctorDisponibleDTO(
                        rs.getInt("id_doctor"),
                        rs.getString("nombre_completo"),
                        rs.getString("especialidad")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando doctores: " + e.getMessage());
        }
        return lista;
    }

    // --- Buscar doctor por nombre completo (para agendar cita) ---
    public Optional<Integer> findIdByNombreCompleto(String nombreCompleto) {
        // El frontend envía el nombre como "Dra. María Torres"
        // La BD guarda nombre y apellido separados, así que buscamos
        // por CONCAT. Si hay tildes o prefijos (Dra./Dr.), hacemos LIKE.
        String sql =
            "SELECT d.id_doctor " +
            "FROM doctores d " +
            "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
            "WHERE CONCAT(u.nombre, ' ', u.apellido) = ? " +
            "  AND u.estado = 'ACTIVO' " +
            "LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreCompleto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt("id_doctor"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando doctor por nombre: " + e.getMessage());
        }
        return Optional.empty();
    }

    // --- Buscar doctor por ID (para construir la respuesta) ---
    public Optional<DoctorDisponibleDTO> findById(Integer idDoctor) {
        String sql =
            "SELECT d.id_doctor, " +
            "       CONCAT(u.nombre, ' ', u.apellido) AS nombre_completo, " +
            "       d.especialidad " +
            "FROM doctores d " +
            "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
            "WHERE d.id_doctor = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new DoctorDisponibleDTO(
                        rs.getInt("id_doctor"),
                        rs.getString("nombre_completo"),
                        rs.getString("especialidad")
                    ));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando doctor por id: " + e.getMessage());
        }
        return Optional.empty();
    }
}
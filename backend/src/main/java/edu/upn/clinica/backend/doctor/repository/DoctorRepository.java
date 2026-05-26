package edu.upn.clinica.backend.doctor.repository;

import edu.upn.clinica.backend.doctor.dto.DoctorDTO;
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
        // El frontend envía el nombre como "Dra. María Torres" o "Dr. Carlos Mendoza".
        // La BD guarda nombre y apellido sin prefijos, así que limpiamos.
        String nombreLimpio = nombreCompleto
                .replaceAll("^(Dr\\.|Dra\\.|Lic\\.)\\s*", "")
                .trim();

        String sql =
            "SELECT d.id_doctor " +
            "FROM doctores d " +
            "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
            "WHERE CONCAT(u.nombre, ' ', u.apellido) = ? " +
            "  AND u.estado = 'ACTIVO' " +
            "LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreLimpio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt("id_doctor"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando doctor por nombre: " + e.getMessage());
        }
        return Optional.empty();
    }

    // --- Listar todos los doctores activos ---
    public List<DoctorDisponibleDTO> findAll() {
        String sql =
            "SELECT d.id_doctor, " +
            "       CONCAT(u.nombre, ' ', u.apellido) AS nombre_completo, " +
            "       d.especialidad " +
            "FROM doctores d " +
            "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
            "WHERE u.estado = 'ACTIVO' " +
            "ORDER BY u.nombre";

        List<DoctorDisponibleDTO> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new DoctorDisponibleDTO(
                    rs.getInt("id_doctor"),
                    rs.getString("nombre_completo"),
                    rs.getString("especialidad")
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando doctores: " + e.getMessage());
        }
        return lista;
    }

    // --- Buscar doctor por email del usuario asociado ---
    public Optional<Integer> findIdByEmail(String email) {
        String sql =
            "SELECT d.id_doctor " +
            "FROM doctores d " +
            "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
            "WHERE u.email = ? AND u.estado = 'ACTIVO' " +
            "LIMIT 1";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt("id_doctor"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando doctor por email: " + e.getMessage());
        }
        return Optional.empty();
    }

    // --- Listar todos los doctores para admin (con datos completos) ---
    public List<DoctorDTO> findAllAdmin() {
        String sql =
            "SELECT d.id_doctor, u.nombre, u.apellido, u.email, " +
            "       d.especialidad, u.telefono, u.estado " +
            "FROM doctores d " +
            "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
            "ORDER BY u.nombre, u.apellido";

        List<DoctorDTO> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DoctorDTO dto = new DoctorDTO();
                dto.setIdDoctor(rs.getInt("id_doctor"));
                dto.setNombre(rs.getString("nombre"));
                dto.setApellido(rs.getString("apellido"));
                dto.setEmail(rs.getString("email"));
                dto.setEspecialidad(rs.getString("especialidad"));
                dto.setTelefono(rs.getString("telefono"));
                dto.setEstado(rs.getString("estado"));
                lista.add(dto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando doctores para admin: " + e.getMessage());
        }
        return lista;
    }

    // --- Actualizar especialidad de un doctor ---
    public void updateEspecialidad(Integer idDoctor, String especialidad) {
        String sql = "UPDATE doctores SET especialidad = ? WHERE id_doctor = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, especialidad);
            ps.setInt(2, idDoctor);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando especialidad: " + e.getMessage());
        }
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
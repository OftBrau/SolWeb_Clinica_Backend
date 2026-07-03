package edu.upn.clinica.backend.doctor.repository;

import edu.upn.clinica.backend.doctor.dto.ActualizarDoctorRequest;
import edu.upn.clinica.backend.doctor.dto.CrearDoctorRequest;
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
            "       d.especialidad, " +
            "       d.foto_url, " +
            "       d.descripcion, " +
            "       d.bibliografia " +
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
                    DoctorDisponibleDTO dto = new DoctorDisponibleDTO(
                        rs.getInt("id_doctor"),
                        rs.getString("nombre_completo"),
                        rs.getString("especialidad"),
                        rs.getString("foto_url")
                    );
                    dto.setDescripcion(rs.getString("descripcion"));
                    dto.setBibliografia(rs.getString("bibliografia"));
                    lista.add(dto);
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
            "       d.especialidad, " +
            "       d.foto_url, " +
            "       d.descripcion, " +
            "       d.bibliografia " +
            "FROM doctores d " +
            "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
            "WHERE u.estado = 'ACTIVO' AND u.rol IN ('DOCTOR', 'MEDICO') AND d.destacado = TRUE " +
            "ORDER BY u.nombre";

        List<DoctorDisponibleDTO> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                DoctorDisponibleDTO dto = new DoctorDisponibleDTO(
                    rs.getInt("id_doctor"),
                    rs.getString("nombre_completo"),
                    rs.getString("especialidad"),
                    rs.getString("foto_url")
                );
                dto.setDescripcion(rs.getString("descripcion"));
                dto.setBibliografia(rs.getString("bibliografia"));
                lista.add(dto);
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
            "       d.especialidad, d.CMP, d.foto_url, u.telefono, u.estado, d.destacado " +
            "FROM doctores d " +
            "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
            "WHERE u.rol IN ('DOCTOR', 'MEDICO') " +
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
                dto.setCmp(rs.getString("CMP"));
                dto.setFotoUrl(rs.getString("foto_url"));
                dto.setTelefono(rs.getString("telefono"));
                dto.setEstado(rs.getString("estado"));
                dto.setDestacado(rs.getBoolean("destacado"));
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
            "       d.especialidad, " +
            "       d.descripcion, " +
            "       d.bibliografia " +
            "FROM doctores d " +
            "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
            "WHERE d.id_doctor = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DoctorDisponibleDTO dto = new DoctorDisponibleDTO(
                        rs.getInt("id_doctor"),
                        rs.getString("nombre_completo"),
                        rs.getString("especialidad")
                    );
                    dto.setDescripcion(rs.getString("descripcion"));
                    dto.setBibliografia(rs.getString("bibliografia"));
                    return Optional.of(dto);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando doctor por id: " + e.getMessage());
        }
        return Optional.empty();
    }

    // --- Verificar si el email ya existe (para crear doctor) ---
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error verificando email: " + e.getMessage());
        }
    }

    // --- Crear doctor (inserta en usuarios + doctores con TX manual) ---
    public void save(CrearDoctorRequest request, String passwordHash) {
        String sqlUsuario = "INSERT INTO usuarios (nombre, apellido, email, password_hash, telefono, rol, estado) "
                + "VALUES (?, ?, ?, ?, ?, 'DOCTOR', 'ACTIVO')";
        String sqlDoctor = "INSERT INTO doctores (id_usuario, especialidad, CMP) VALUES (?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, request.getNombre());
                ps.setString(2, request.getApellido());
                ps.setString(3, request.getEmail());
                ps.setString(4, passwordHash);
                ps.setString(5, request.getTelefono());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (!rs.next()) throw new SQLException("No se generó id_usuario");
                    int idUsuario = rs.getInt(1);

                    String cmp = request.getCmp();
                    if (cmp == null || cmp.isBlank()) {
                        cmp = "CMP-" + String.format("%06d", idUsuario);
                    }

                    try (PreparedStatement ps2 = conn.prepareStatement(sqlDoctor)) {
                        ps2.setInt(1, idUsuario);
                        ps2.setString(2, request.getEspecialidad());
                        ps2.setString(3, cmp);
                        ps2.executeUpdate();
                    }
                }
            }
            conn.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error creando doctor: " + e.getMessage());
        }
    }

    // --- Actualizar doctor (actualiza usuarios + doctores con TX manual) ---
    public void update(Integer idDoctor, ActualizarDoctorRequest request) {
        String sqlFind = "SELECT id_usuario FROM doctores WHERE id_doctor = ?";
        String sqlUsuario = "UPDATE usuarios SET nombre = ?, apellido = ?, telefono = ? WHERE id_usuario = ?";
        String sqlDoctor = "UPDATE doctores SET especialidad = ?, CMP = ? WHERE id_doctor = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sqlFind)) {
                ps.setInt(1, idDoctor);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Doctor no encontrado: " + idDoctor);
                    int idUsuario = rs.getInt("id_usuario");

                    try (PreparedStatement ps2 = conn.prepareStatement(sqlUsuario)) {
                        ps2.setString(1, request.getNombre());
                        ps2.setString(2, request.getApellido());
                        ps2.setString(3, request.getTelefono());
                        ps2.setInt(4, idUsuario);
                        ps2.executeUpdate();
                    }

                    try (PreparedStatement ps3 = conn.prepareStatement(sqlDoctor)) {
                        ps3.setString(1, request.getEspecialidad());
                        ps3.setString(2, request.getCmp());
                        ps3.setInt(3, idDoctor);
                        ps3.executeUpdate();
                    }
                }
            }
            conn.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando doctor: " + e.getMessage());
        }
    }

    // --- Actualizar foto del doctor ---
    public void updateFoto(Integer idDoctor, String fotoUrl) {
        String sql = "UPDATE doctores SET foto_url = ? WHERE id_doctor = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fotoUrl);
            ps.setInt(2, idDoctor);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando foto: " + e.getMessage());
        }
    }

    // --- Eliminar doctor (soft-delete: estado = INACTIVO) ---
    public void deleteById(Integer idDoctor) {
        String sqlFind = "SELECT id_usuario FROM doctores WHERE id_doctor = ?";
        String sqlEstado = "UPDATE usuarios SET estado = 'INACTIVO' WHERE id_usuario = ?";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sqlFind)) {
                ps.setInt(1, idDoctor);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Doctor no encontrado: " + idDoctor);
                    int idUsuario = rs.getInt("id_usuario");

                    try (PreparedStatement ps2 = conn.prepareStatement(sqlEstado)) {
                        ps2.setInt(1, idUsuario);
                        ps2.executeUpdate();
                    }
                }
            }
            conn.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando doctor: " + e.getMessage());
        }
    }

    public void reactivateById(Integer idDoctor) {
        String sqlFind = "SELECT id_usuario FROM doctores WHERE id_doctor = ?";
        String sqlEstado = "UPDATE usuarios SET estado = 'ACTIVO' WHERE id_usuario = ?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sqlFind)) {
                ps.setInt(1, idDoctor);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Doctor no encontrado: " + idDoctor);
                    int idUsuario = rs.getInt("id_usuario");
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlEstado)) {
                        ps2.setInt(1, idUsuario);
                        ps2.executeUpdate();
                    }
                }
            }
            conn.commit();
        } catch (Exception e) {
            throw new RuntimeException("Error reactivando doctor: " + e.getMessage());
        }
    }

    public void toggleDestacado(Integer idDoctor) {
        String sql = "UPDATE doctores SET destacado = NOT destacado WHERE id_doctor = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error toggle destacado: " + e.getMessage());
        }
    }
}
package edu.upn.clinica.backend.auth;

import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

// ============================================================
//  UsuarioRepository.java
//  Acceso a la tabla usuarios en MySQL
// ============================================================
@Repository
public class UsuarioRepository extends BaseRepository {

    // --- Buscar usuario por email ---
    public Optional<Usuario> findByEmail(String email) {
        String sql = "SELECT id_usuario, nombre, apellido, email, " +
                     "password_hash, telefono, rol, estado " +
                     "FROM usuarios WHERE email = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando usuario: " + e.getMessage());
        }

        return Optional.empty();
    }

    // --- Mapear ResultSet → Usuario ---
    private Usuario mapRow(ResultSet rs) throws Exception {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id_usuario"));
        u.setNombre(rs.getString("nombre"));
        u.setApellido(rs.getString("apellido"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setTelefono(rs.getString("telefono"));
        u.setRol(rs.getString("rol"));
        u.setEstado(rs.getString("estado"));
        return u;
    }
}
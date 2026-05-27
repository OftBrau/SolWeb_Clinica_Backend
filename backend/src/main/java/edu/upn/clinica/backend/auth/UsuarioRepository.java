package edu.upn.clinica.backend.auth;

import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// ============================================================
//  UsuarioRepository.java
//  Acceso a la tabla usuarios en MySQL
// ============================================================
@Repository
public class UsuarioRepository extends BaseRepository {

    private static final String SELECT_COLUMNS =
            "SELECT id_usuario, nombre, apellido, email, " +
            "password_hash, telefono, rol, estado, password_default ";

    private static final String FROM_TABLE = "FROM usuarios";

    // --- Buscar usuario por email ---
    public Optional<Usuario> findByEmail(String email) {
        String sql = SELECT_COLUMNS + FROM_TABLE + " WHERE email = ?";

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

    // --- Listar usuarios con paginación ---
    public List<Usuario> findAll(int page, int size) {
        String sql = SELECT_COLUMNS + FROM_TABLE +
                     " ORDER BY id_usuario LIMIT ? OFFSET ?";
        List<Usuario> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, page * size);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando usuarios: " + e.getMessage());
        }

        return list;
    }

    // --- Contar total de usuarios ---
    public long count() {
        String sql = "SELECT COUNT(*) " + FROM_TABLE;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error contando usuarios: " + e.getMessage());
        }

        return 0;
    }

    // --- Buscar usuario por ID ---
    public Optional<Usuario> findById(Integer id) {
        String sql = SELECT_COLUMNS + FROM_TABLE + " WHERE id_usuario = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando usuario por id: " + e.getMessage());
        }

        return Optional.empty();
    }

    // --- Insertar nuevo usuario ---
    public Usuario save(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, apellido, email, password_hash, telefono, rol, estado, password_default) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getPasswordHash());
            ps.setString(5, usuario.getTelefono());
            ps.setString(6, usuario.getRol());
            ps.setString(7, usuario.getEstado());
            ps.setBoolean(8, usuario.isPasswordDefault());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
            return usuario;

        } catch (Exception e) {
            throw new RuntimeException("Error creando usuario: " + e.getMessage());
        }
    }

    // --- Verificar si el email ya existe ---
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

    // --- Actualizar datos de usuario ---
    public void update(Integer id, String nombre, String apellido, String telefono) {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, telefono = ? WHERE id_usuario = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, telefono);
            ps.setInt(4, id);
            int affected = ps.executeUpdate();

            if (affected == 0) {
                throw new RuntimeException("Usuario no encontrado con id: " + id);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando usuario: " + e.getMessage());
        }
    }

    // --- Actualizar contraseña ---
    public void updatePassword(Integer id, String passwordHash) {
        String sql = "UPDATE usuarios SET password_hash = ?, password_default = 0 WHERE id_usuario = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando contraseña: " + e.getMessage());
        }
    }

    // --- Actualizar datos personales ---
    public void updatePerfil(Integer id, String nombre, String apellido, String telefono) {
        String sql = "UPDATE usuarios SET nombre = ?, apellido = ?, telefono = ? WHERE id_usuario = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, apellido);
            ps.setString(3, telefono);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando perfil: " + e.getMessage());
        }
    }

    // --- Actualizar estado de usuario (ACTIVO / INACTIVO) ---
    public void updateEstado(Integer id, String estado) {
        String sql = "UPDATE usuarios SET estado = ? WHERE id_usuario = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, id);
            int affected = ps.executeUpdate();

            if (affected == 0) {
                throw new RuntimeException("Usuario no encontrado con id: " + id);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando estado: " + e.getMessage());
        }
    }

    // --- Actualizar rol de usuario ---
    public void updateRol(Integer id, String rol) {
        String sql = "UPDATE usuarios SET rol = ? WHERE id_usuario = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, rol);
            ps.setInt(2, id);
            int affected = ps.executeUpdate();

            if (affected == 0) {
                throw new RuntimeException("Usuario no encontrado con id: " + id);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando rol: " + e.getMessage());
        }
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
        u.setPasswordDefault(rs.getBoolean("password_default"));
        return u;
    }
}
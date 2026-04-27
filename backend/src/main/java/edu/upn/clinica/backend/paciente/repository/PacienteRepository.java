package edu.upn.clinica.backend.paciente.repository;

import edu.upn.clinica.backend.paciente.model.Paciente;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// ============================================================
//  PacienteRepository.java
//  SQL puro con JDBC — tablas: usuarios + pacientes
// ============================================================
@Repository
public class PacienteRepository extends BaseRepository {

    private static final String SELECT_BASE =
        "SELECT p.id_paciente, p.id_usuario, u.nombre, u.apellido, u.email, " +
        "u.telefono, u.estado, p.codigo_estudiante, p.fecha_nacimiento, " +
        "p.genero, p.tipo_sangre, p.alergias " +
        "FROM pacientes p " +
        "JOIN usuarios u ON p.id_usuario = u.id_usuario ";

    // --- Listar todos con paginación ---
    public List<Paciente> findAll(int page, int size) {
        String sql = SELECT_BASE + "ORDER BY p.id_paciente LIMIT ? OFFSET ?";
        List<Paciente> lista = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, page * size);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando pacientes: " + e.getMessage());
        }
        return lista;
    }

    // --- Contar total (para paginación) ---
    public long count() {
        String sql = "SELECT COUNT(*) FROM pacientes";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando pacientes: " + e.getMessage());
        }
    }

    // --- Buscar por ID ---
    public Optional<Paciente> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE p.id_paciente = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando paciente: " + e.getMessage());
        }
        return Optional.empty();
    }

    // --- Buscar por email ---
    public Optional<Paciente> findByEmail(String email) {
        String sql = SELECT_BASE + "WHERE u.email = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando paciente por email: " + e.getMessage());
        }
        return Optional.empty();
    }

    // --- Verificar si email ya existe ---
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

    // --- Guardar paciente (inserta en usuarios + pacientes + historias_clinicas) ---
    public Paciente save(Paciente p) {
        String sqlUsuario  = "INSERT INTO usuarios (nombre, apellido, email, password_hash, telefono, rol, estado) " +
                             "VALUES (?, ?, ?, ?, ?, 'PACIENTE', 'ACTIVO')";
        String sqlPaciente = "INSERT INTO pacientes (id_usuario, codigo_estudiante, fecha_nacimiento, genero, tipo_sangre, alergias) " +
                             "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlHce      = "INSERT INTO historias_clinicas (id_paciente) VALUES (?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Insertar en usuarios
                PreparedStatement psU = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
                psU.setString(1, p.getNombre());
                psU.setString(2, p.getApellido());
                psU.setString(3, p.getEmail());
                psU.setString(4, p.getPasswordHash());
                psU.setString(5, p.getTelefono());
                psU.executeUpdate();

                ResultSet rsU = psU.getGeneratedKeys();
                int idUsuario = rsU.next() ? rsU.getInt(1) : 0;
                p.setIdUsuario(idUsuario);

                // 2. Insertar en pacientes
                PreparedStatement psP = conn.prepareStatement(sqlPaciente, Statement.RETURN_GENERATED_KEYS);
                psP.setInt(1, idUsuario);
                psP.setString(2, p.getCodigoEstudiante());
                psP.setDate(3, Date.valueOf(p.getFechaNacimiento()));
                psP.setString(4, p.getGenero());
                psP.setString(5, p.getTipoSangre());
                psP.setString(6, p.getAlergias());
                psP.executeUpdate();

                ResultSet rsP = psP.getGeneratedKeys();
                int idPaciente = rsP.next() ? rsP.getInt(1) : 0;
                p.setIdPaciente(idPaciente);

                // 3. Crear HCE automáticamente
                PreparedStatement psH = conn.prepareStatement(sqlHce);
                psH.setInt(1, idPaciente);
                psH.executeUpdate();

                conn.commit();
                return p;

            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error guardando paciente: " + e.getMessage());
        }
    }

    // --- Actualizar paciente ---
    public void update(Paciente p) {
        String sqlU = "UPDATE usuarios SET nombre=?, apellido=?, telefono=? WHERE id_usuario=?";
        String sqlP = "UPDATE pacientes SET codigo_estudiante=?, fecha_nacimiento=?, " +
                      "genero=?, tipo_sangre=?, alergias=? WHERE id_paciente=?";
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                PreparedStatement psU = conn.prepareStatement(sqlU);
                psU.setString(1, p.getNombre());
                psU.setString(2, p.getApellido());
                psU.setString(3, p.getTelefono());
                psU.setInt(4, p.getIdUsuario());
                psU.executeUpdate();

                PreparedStatement psP = conn.prepareStatement(sqlP);
                psP.setString(1, p.getCodigoEstudiante());
                psP.setDate(2, Date.valueOf(p.getFechaNacimiento()));
                psP.setString(3, p.getGenero());
                psP.setString(4, p.getTipoSangre());
                psP.setString(5, p.getAlergias());
                psP.setInt(6, p.getIdPaciente());
                psP.executeUpdate();

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando paciente: " + e.getMessage());
        }
    }

    // --- Desactivar (soft delete) ---
    public void deactivate(Integer idUsuario) {
        String sql = "UPDATE usuarios SET estado='INACTIVO' WHERE id_usuario=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error desactivando paciente: " + e.getMessage());
        }
    }

    // --- Mapear ResultSet → Paciente ---
    private Paciente mapRow(ResultSet rs) throws Exception {
        Paciente p = new Paciente();
        p.setIdPaciente(rs.getInt("id_paciente"));
        p.setIdUsuario(rs.getInt("id_usuario"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setEmail(rs.getString("email"));
        p.setTelefono(rs.getString("telefono"));
        p.setEstado(rs.getString("estado"));
        p.setCodigoEstudiante(rs.getString("codigo_estudiante"));
        p.setFechaNacimiento(rs.getDate("fecha_nacimiento").toLocalDate());
        p.setGenero(rs.getString("genero"));
        p.setTipoSangre(rs.getString("tipo_sangre"));
        p.setAlergias(rs.getString("alergias"));
        return p;
    }
}
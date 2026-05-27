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

    private static final String SELECT_BASE
            = "SELECT p.id_paciente, p.id_usuario, u.nombre, u.apellido, u.email, "
            + "u.telefono, u.estado, p.codigo_estudiante, p.fecha_nacimiento, "
            + "p.genero, p.tipo_sangre, p.alergias "
            + "FROM pacientes p "
            + "JOIN usuarios u ON p.id_usuario = u.id_usuario ";

    // --- Listar todos con paginación ---
    public List<Paciente> findAll(int page, int size) {
        String sql = SELECT_BASE + "ORDER BY p.id_paciente LIMIT ? OFFSET ?";
        List<Paciente> lista = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, size);
            ps.setInt(2, page * size);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando pacientes: " + e.getMessage());
        }
        return lista;
    }

    // --- Contar total (para paginación) ---
    public long count() {
        String sql = "SELECT COUNT(*) FROM pacientes";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando pacientes: " + e.getMessage());
        }
    }

    // --- Buscar por ID ---
    public Optional<Paciente> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE p.id_paciente = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando paciente: " + e.getMessage());
        }
        return Optional.empty();
    }

    // --- Buscar por email ---
    public Optional<Paciente> findByEmail(String email) {
        String sql = SELECT_BASE + "WHERE u.email = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando paciente por email: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Paciente> findByEmailAndCodigo(String email, String codigo) {
        System.out.println(">>> email: [" + email + "]");
        System.out.println(">>> codigo: [" + codigo + "]");
        String sql = SELECT_BASE
                + "WHERE u.email = ? AND p.codigo_estudiante = ? AND u.estado = 'ACTIVO'";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando paciente por email y código: " + e.getMessage());
        }
        return Optional.empty();
    }

    // --- Verificar si email ya existe ---
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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
        String sql = "{CALL usp_registrar_paciente(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setString(1, p.getNombre());
            cs.setString(2, p.getApellido());
            cs.setString(3, p.getEmail());
            cs.setString(4, p.getPasswordHash());
            cs.setString(5, p.getTelefono());
            cs.setString(6, p.getCodigoEstudiante());
            cs.setDate(7, Date.valueOf(p.getFechaNacimiento()));
            cs.setString(8, p.getGenero());
            cs.setString(9, p.getTipoSangre());
            cs.setString(10, p.getAlergias());
            cs.registerOutParameter(11, Types.INTEGER);
            cs.registerOutParameter(12, Types.INTEGER);

            cs.execute();

            p.setIdUsuario(cs.getInt(11));
            p.setIdPaciente(cs.getInt(12));

            return p;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando paciente: " + e.getMessage());
        }
    }

    // --- Actualizar paciente ---
    public void update(Paciente p) {
        String sql = "{CALL usp_actualizar_paciente(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, p.getIdPaciente());
            cs.setString(2, p.getNombre());
            cs.setString(3, p.getApellido());
            cs.setString(4, p.getTelefono());
            cs.setString(5, p.getCodigoEstudiante());
            cs.setDate(6, Date.valueOf(p.getFechaNacimiento()));
            cs.setString(7, p.getGenero());
            cs.setString(8, p.getTipoSangre());
            cs.setString(9, p.getAlergias());

            cs.execute();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando paciente: " + e.getMessage());
        }
    }

    // --- Desactivar (soft delete) ---
    public void deactivate(Integer idUsuario) {
        String sql = "UPDATE usuarios SET estado='INACTIVO' WHERE id_usuario=?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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

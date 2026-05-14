package edu.upn.clinica.backend.cita.repository;

import edu.upn.clinica.backend.cita.model.Cita;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// ============================================================
//  CitaRepository.java
//  SQL puro con JDBC — tabla: citas
// ============================================================
@Repository
public class CitaRepository extends BaseRepository {

    private static final String SELECT_BASE
            = "SELECT id_cita, id_paciente, id_doctor, id_consultorio, "
            + "fecha, hora, estado, tipo, motivo "
            + "FROM citas ";

    // --- Insertar nueva cita ---
    public Cita save(Cita cita) {
        String sql = "INSERT INTO citas (id_paciente, id_doctor, id_consultorio, "
                + "fecha, hora, estado, tipo, motivo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, cita.getIdPaciente());
            ps.setInt(2, cita.getIdDoctor());

            // consultorio es opcional en la tabla (YES en DDL)
            if (cita.getIdConsultorio() != null) {
                ps.setInt(3, cita.getIdConsultorio());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setDate(4, Date.valueOf(cita.getFecha()));
            ps.setTime(5, Time.valueOf(cita.getHora()));
            ps.setString(6, cita.getEstado());
            ps.setString(7, cita.getTipo());
            ps.setString(8, cita.getMotivo());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                cita.setIdCita(rs.getInt(1));
            }
            return cita;

        } catch (Exception e) {
            throw new RuntimeException("Error guardando cita: " + e.getMessage());
        }
    }

    // --- Cancelar cita ---
    public void cancelar(Integer idCita) {
        String sql = "UPDATE citas SET estado = 'CANCELADA' WHERE id_cita = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error cancelando cita: " + e.getMessage());
        }
    }

// --- Reprogramar cita ---
    public void reprogramar(Integer idCita, LocalDate nuevaFecha, LocalTime nuevaHora) {
        String sql = "UPDATE citas SET fecha = ?, hora = ?, estado = 'CONFIRMADA' WHERE id_cita = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(nuevaFecha));
            ps.setTime(2, Time.valueOf(nuevaHora));
            ps.setInt(3, idCita);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error reprogramando cita: " + e.getMessage());
        }
    }

    // --- Verificar si ya existe una cita en ese horario para ese doctor ---
    public boolean existeConflicto(Integer idDoctor, LocalDate fecha, LocalTime hora) {
        String sql = "SELECT COUNT(*) FROM citas "
                + "WHERE id_doctor = ? AND fecha = ? AND hora = ? "
                + "AND estado NOT IN ('CANCELADA','NO_ASISTIO')";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(hora));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error verificando conflicto de cita: " + e.getMessage());
        }
    }

    // --- Listar citas por paciente ---
    public List<Cita> findByPaciente(Integer idPaciente) {
        String sql = SELECT_BASE + "WHERE id_paciente = ? ORDER BY fecha DESC, hora DESC";
        return query(sql, idPaciente);
    }

    // --- Buscar por ID ---
    public Optional<Cita> findById(Integer id) {
        String sql = SELECT_BASE + "WHERE id_cita = ?";
        List<Cita> result = query(sql, id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    // --- Helper: ejecutar query y mapear ---
    private List<Cita> query(String sql, Object... params) {
        List<Cita> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando citas: " + e.getMessage());
        }
        return lista;
    }

    // --- Mapear ResultSet → Cita ---
    private Cita mapRow(ResultSet rs) throws Exception {
        Cita c = new Cita();
        c.setIdCita(rs.getInt("id_cita"));
        c.setIdPaciente(rs.getInt("id_paciente"));
        c.setIdDoctor(rs.getInt("id_doctor"));
        int idCons = rs.getInt("id_consultorio");
        if (!rs.wasNull()) {
            c.setIdConsultorio(idCons);
        }
        c.setFecha(rs.getDate("fecha").toLocalDate());
        c.setHora(rs.getTime("hora").toLocalTime());
        c.setEstado(rs.getString("estado"));
        c.setTipo(rs.getString("tipo"));
        c.setMotivo(rs.getString("motivo"));
        return c;
    }
}

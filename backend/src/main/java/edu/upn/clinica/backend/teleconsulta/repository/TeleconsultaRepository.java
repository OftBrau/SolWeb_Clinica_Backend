package edu.upn.clinica.backend.teleconsulta.repository;

import edu.upn.clinica.backend.shared.BaseRepository;
import edu.upn.clinica.backend.teleconsulta.model.Teleconsulta;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TeleconsultaRepository extends BaseRepository {

    private static final String SELECT =
        "SELECT id_teleconsulta, id_cita, id_paciente, id_doctor, especialidad, " +
        "url_sesion, fecha, hora, estado, motivo, created_at FROM teleconsultas";

    public Teleconsulta save(Teleconsulta t) {
        String sql = "INSERT INTO teleconsultas (id_cita, id_paciente, id_doctor, especialidad, " +
                     "url_sesion, fecha, hora, estado, motivo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (t.getIdCita() != null) {
                ps.setInt(1, t.getIdCita());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setInt(2, t.getIdPaciente());
            if (t.getIdDoctor() != null) {
                ps.setInt(3, t.getIdDoctor());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, t.getEspecialidad());
            ps.setString(5, t.getUrlSesion());
            ps.setDate(6, Date.valueOf(t.getFecha()));
            ps.setTime(7, Time.valueOf(t.getHora()));
            ps.setString(8, t.getEstado());
            ps.setString(9, t.getMotivo());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) t.setIdTeleconsulta(rs.getInt(1));
            return t;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando teleconsulta: " + e.getMessage());
        }
    }

    public Optional<Teleconsulta> findById(Integer id) {
        String sql = SELECT + " WHERE id_teleconsulta = ?";
        List<Teleconsulta> list = query(sql, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    public List<Teleconsulta> findByPaciente(Integer idPaciente) {
        return query(SELECT + " WHERE id_paciente = ? ORDER BY fecha DESC, hora DESC", idPaciente);
    }

    public List<Teleconsulta> findByDoctor(Integer idDoctor) {
        return query(SELECT + " WHERE id_doctor = ? ORDER BY fecha DESC, hora DESC", idDoctor);
    }

    public List<Teleconsulta> findAll() {
        return query(SELECT + " ORDER BY fecha DESC, hora DESC");
    }

    public Optional<Teleconsulta> findByCitaId(Integer idCita) {
        List<Teleconsulta> list = query(SELECT + " WHERE id_cita = ?", idCita);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    public void actualizarEstado(Integer id, String estado) {
        String sql = "UPDATE teleconsultas SET estado = ? WHERE id_teleconsulta = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando estado de teleconsulta: " + e.getMessage());
        }
    }

    private List<Teleconsulta> query(String sql, Object... params) {
        List<Teleconsulta> list = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando teleconsultas: " + e.getMessage());
        }
        return list;
    }

    private Teleconsulta mapRow(ResultSet rs) throws Exception {
        Teleconsulta t = new Teleconsulta();
        t.setIdTeleconsulta(rs.getInt("id_teleconsulta"));
        int idc = rs.getInt("id_cita");
        if (!rs.wasNull()) t.setIdCita(idc);
        t.setIdPaciente(rs.getInt("id_paciente"));
        int idDoc = rs.getInt("id_doctor");
        if (!rs.wasNull()) t.setIdDoctor(idDoc);
        t.setEspecialidad(rs.getString("especialidad"));
        t.setUrlSesion(rs.getString("url_sesion"));
        t.setFecha(rs.getDate("fecha").toLocalDate());
        t.setHora(rs.getTime("hora").toLocalTime());
        t.setEstado(rs.getString("estado"));
        t.setMotivo(rs.getString("motivo"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) t.setCreatedAt(ca.toLocalDateTime());
        return t;
    }
}

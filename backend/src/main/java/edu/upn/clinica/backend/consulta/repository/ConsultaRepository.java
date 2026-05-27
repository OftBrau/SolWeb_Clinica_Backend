package edu.upn.clinica.backend.consulta.repository;

import edu.upn.clinica.backend.consulta.model.Consulta;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ConsultaRepository extends BaseRepository {

    private static final String SELECT =
        "SELECT id_consulta, id_cita, id_paciente, id_doctor, " +
        "diagnostico_cie10, descripcion_diagnostico, tratamiento, " +
        "prescripcion, id_practicante, estado_revision, created_at, updated_at FROM consultas";

    public Consulta save(Consulta c) {
        String sql = "{CALL usp_iniciar_consulta(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, c.getIdCita());
            cs.setInt(2, c.getIdPaciente());
            cs.setInt(3, c.getIdDoctor());
            cs.setString(4, c.getDiagnosticoCie10());
            cs.setString(5, c.getDescripcionDiagnostico());
            cs.setString(6, c.getTratamiento());
            cs.setString(7, c.getPrescripcion());
            if (c.getIdPracticante() != null) {
                cs.setInt(8, c.getIdPracticante());
            } else {
                cs.setNull(8, Types.INTEGER);
            }
            cs.registerOutParameter(9, Types.INTEGER);

            cs.execute();

            c.setIdConsulta(cs.getInt(9));
            return c;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando consulta: " + e.getMessage());
        }
    }

    public Optional<Consulta> findById(Integer id) {
        String sql = SELECT + " WHERE id_consulta = ?";
        List<Consulta> list = query(sql, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    public Optional<Consulta> findByCitaId(Integer idCita) {
        String sql = SELECT + " WHERE id_cita = ?";
        List<Consulta> list = query(sql, idCita);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    public List<Consulta> findByPaciente(Integer idPaciente) {
        return query(SELECT + " WHERE id_paciente = ? ORDER BY created_at DESC", idPaciente);
    }

    public void updateDiagnostico(Integer id, String cie10, String descripcion) {
        String sql = "UPDATE consultas SET diagnostico_cie10 = ?, descripcion_diagnostico = ?, updated_at = NOW() WHERE id_consulta = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cie10);
            ps.setString(2, descripcion);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando diagnóstico: " + e.getMessage());
        }
    }

    public void updateTratamiento(Integer id, String tratamiento) {
        String sql = "UPDATE consultas SET tratamiento = ?, updated_at = NOW() WHERE id_consulta = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tratamiento);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando tratamiento: " + e.getMessage());
        }
    }

    public void updatePrescripcion(Integer id, String prescripcion) {
        String sql = "UPDATE consultas SET prescripcion = ?, updated_at = NOW() WHERE id_consulta = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prescripcion);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando prescripción: " + e.getMessage());
        }
    }

    private List<Consulta> query(String sql, Object... params) {
        List<Consulta> list = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando: " + e.getMessage());
        }
        return list;
    }

    private Consulta mapRow(ResultSet rs) throws Exception {
        Consulta c = new Consulta();
        c.setIdConsulta(rs.getInt("id_consulta"));
        c.setIdCita(rs.getInt("id_cita"));
        c.setIdPaciente(rs.getInt("id_paciente"));
        c.setIdDoctor(rs.getInt("id_doctor"));
        c.setDiagnosticoCie10(rs.getString("diagnostico_cie10"));
        c.setDescripcionDiagnostico(rs.getString("descripcion_diagnostico"));
        c.setTratamiento(rs.getString("tratamiento"));
        c.setPrescripcion(rs.getString("prescripcion"));
        int idp = rs.getInt("id_practicante");
        if (!rs.wasNull()) c.setIdPracticante(idp);
        c.setEstadoRevision(rs.getString("estado_revision"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) c.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) c.setUpdatedAt(ua.toLocalDateTime());
        return c;
    }
}

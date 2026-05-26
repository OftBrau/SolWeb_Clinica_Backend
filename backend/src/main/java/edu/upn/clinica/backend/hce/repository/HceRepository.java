package edu.upn.clinica.backend.hce.repository;

import edu.upn.clinica.backend.hce.model.HistorialItem;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class HceRepository extends BaseRepository {

    public List<HistorialItem> findAll() {
        String sql = "SELECT v.*, c.id_consulta FROM vista_historial_paciente v " +
                     "LEFT JOIN citas ct ON ct.id_paciente = v.id_paciente AND ct.fecha = DATE(v.fecha) " +
                     "LEFT JOIN consultas c ON c.id_cita = ct.id_cita " +
                     "ORDER BY v.fecha DESC";
        List<HistorialItem> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (Exception e) {
            throw new RuntimeException("Error consultando historial completo: " + e.getMessage());
        }
        return lista;
    }

    public List<HistorialItem> findByPaciente(Integer idPaciente) {
        String sql = "SELECT v.*, c.id_consulta FROM vista_historial_paciente v " +
                     "LEFT JOIN citas ct ON ct.id_paciente = v.id_paciente AND ct.fecha = DATE(v.fecha) " +
                     "LEFT JOIN consultas c ON c.id_cita = ct.id_cita " +
                     "WHERE v.id_paciente = ? ORDER BY v.fecha DESC";
        List<HistorialItem> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando historial: " + e.getMessage());
        }
        return lista;
    }

    public Optional<HistorialItem> findByIdConsulta(Integer idConsulta) {
        String sql = "SELECT v.*, c.id_consulta FROM consultas c " +
                     "JOIN citas ct ON ct.id_cita = c.id_cita " +
                     "LEFT JOIN vista_historial_paciente v ON v.id_paciente = c.id_paciente " +
                     "  AND v.fecha = ct.fecha " +
                     "WHERE c.id_consulta = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando historial: " + e.getMessage());
        }
        return Optional.empty();
    }

    private HistorialItem mapRow(ResultSet rs) throws Exception {
        HistorialItem h = new HistorialItem();
        int idc = rs.getInt("id_consulta");
        h.setIdConsulta(rs.wasNull() ? null : idc);
        h.setIdPaciente(rs.getInt("id_paciente"));
        h.setNombrePaciente(rs.getString("nombre_paciente"));
        h.setCodigoEstudiante(rs.getString("codigo_estudiante"));
        Timestamp tsFecha = rs.getTimestamp("fecha");
        if (tsFecha != null) h.setFecha(tsFecha.toLocalDateTime());
        h.setDiagnosticoCie10(rs.getString("diagnostico_cie10"));
        h.setDescripcionDiag(rs.getString("descripcion_diag"));
        h.setTratamiento(rs.getString("tratamiento"));
        h.setPrescripcion(rs.getString("prescripcion"));
        h.setNombreDoctor(rs.getString("nombre_doctor"));
        h.setEspecialidad(rs.getString("especialidad"));
        return h;
    }
}
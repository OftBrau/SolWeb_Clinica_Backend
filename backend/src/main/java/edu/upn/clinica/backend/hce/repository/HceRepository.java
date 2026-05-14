package edu.upn.clinica.backend.hce.repository;

import edu.upn.clinica.backend.hce.model.HistorialItem;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HceRepository extends BaseRepository {

    public List<HistorialItem> findByPaciente(Integer idPaciente) {
        String sql = "SELECT * FROM vista_historial_paciente WHERE id_paciente = ? ORDER BY fecha DESC";
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

    private HistorialItem mapRow(ResultSet rs) throws Exception {
        HistorialItem h = new HistorialItem();
        h.setIdPaciente(rs.getInt("id_paciente"));
        h.setNombrePaciente(rs.getString("nombre_paciente"));
        h.setCodigoEstudiante(rs.getString("codigo_estudiante"));
        h.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        h.setDiagnosticoCie10(rs.getString("diagnostico_cie10"));
        h.setDescripcionDiag(rs.getString("descripcion_diag"));
        h.setTratamiento(rs.getString("tratamiento"));
        h.setPrescripcion(rs.getString("prescripcion"));
        h.setNombreDoctor(rs.getString("nombre_doctor"));
        h.setEspecialidad(rs.getString("especialidad"));
        return h;
    }
}
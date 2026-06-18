package edu.upn.clinica.backend.teleconsulta.nota.repository;

import edu.upn.clinica.backend.shared.BaseRepository;
import edu.upn.clinica.backend.teleconsulta.nota.model.NotaTeleconsulta;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class NotaTeleconsultaRepository extends BaseRepository {

    public NotaTeleconsulta save(NotaTeleconsulta nota) {
        String sql = "INSERT INTO notas_teleconsulta (id_teleconsulta, id_doctor, contenido, tipo) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, nota.getIdTeleconsulta());
            ps.setInt(2, nota.getIdDoctor());
            ps.setString(3, nota.getContenido());
            ps.setString(4, nota.getTipo());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) nota.setIdNota(rs.getInt(1));
            return nota;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando nota: " + e.getMessage());
        }
    }

    public List<NotaTeleconsulta> findByTeleconsulta(Integer idTeleconsulta) {
        String sql = "SELECT id_nota, id_teleconsulta, id_doctor, contenido, tipo, created_at FROM notas_teleconsulta WHERE id_teleconsulta = ? ORDER BY created_at ASC";
        List<NotaTeleconsulta> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTeleconsulta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    NotaTeleconsulta n = new NotaTeleconsulta();
                    n.setIdNota(rs.getInt("id_nota"));
                    n.setIdTeleconsulta(rs.getInt("id_teleconsulta"));
                    n.setIdDoctor(rs.getInt("id_doctor"));
                    n.setContenido(rs.getString("contenido"));
                    n.setTipo(rs.getString("tipo"));
                    Timestamp ca = rs.getTimestamp("created_at");
                    if (ca != null) n.setCreatedAt(ca.toLocalDateTime());
                    list.add(n);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando notas: " + e.getMessage());
        }
        return list;
    }
}

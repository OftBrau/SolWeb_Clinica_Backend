package edu.upn.clinica.backend.examen.repository;

import edu.upn.clinica.backend.examen.model.Examen;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ExamenRepository extends BaseRepository {

    private static final String SELECT =
        "SELECT e.id_examen, e.id_consulta, e.id_paciente, e.id_doctor, " +
        "e.tipo, e.nombre_examen, e.descripcion, e.resultado, e.estado, e.created_at, e.updated_at " +
        "FROM examenes e";

    public Examen save(Examen e) {
        String sql = "INSERT INTO examenes (id_consulta, id_paciente, id_doctor, tipo, nombre_examen, descripcion, estado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'PENDIENTE')";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getIdConsulta());
            ps.setInt(2, e.getIdPaciente());
            ps.setInt(3, e.getIdDoctor());
            ps.setString(4, e.getTipo());
            ps.setString(5, e.getNombreExamen());
            ps.setString(6, e.getDescripcion());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) e.setIdExamen(rs.getInt(1));
            return e;
        } catch (Exception ex) {
            throw new RuntimeException("Error guardando examen: " + ex.getMessage());
        }
    }

    public List<Examen> findByPaciente(Integer idPaciente) {
        return query(SELECT + " WHERE e.id_paciente = ? ORDER BY e.created_at DESC", idPaciente);
    }

    public List<Examen> findByDoctor(Integer idDoctor) {
        return query(SELECT + " WHERE e.id_doctor = ? ORDER BY e.created_at DESC", idDoctor);
    }

    public List<Examen> findByConsulta(Integer idConsulta) {
        return query(SELECT + " WHERE e.id_consulta = ? ORDER BY e.created_at", idConsulta);
    }

    public Optional<Examen> findById(Integer id) {
        List<Examen> list = query(SELECT + " WHERE e.id_examen = ?", id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    public void actualizarResultado(Integer id, String resultado) {
        String sql = "UPDATE examenes SET resultado = ?, estado = 'RECIBIDO', updated_at = NOW() WHERE id_examen = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, resultado);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception ex) {
            throw new RuntimeException("Error actualizando resultado: " + ex.getMessage());
        }
    }

    private List<Examen> query(String sql, Object... params) {
        List<Examen> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error consultando examenes: " + ex.getMessage());
        }
        return lista;
    }

    private Examen mapRow(ResultSet rs) throws Exception {
        Examen e = new Examen();
        e.setIdExamen(rs.getInt("id_examen"));
        e.setIdConsulta(rs.getInt("id_consulta"));
        e.setIdPaciente(rs.getInt("id_paciente"));
        e.setIdDoctor(rs.getInt("id_doctor"));
        e.setTipo(rs.getString("tipo"));
        e.setNombreExamen(rs.getString("nombre_examen"));
        e.setDescripcion(rs.getString("descripcion"));
        e.setResultado(rs.getString("resultado"));
        e.setEstado(rs.getString("estado"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) e.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) e.setUpdatedAt(ua.toLocalDateTime());
        return e;
    }
}

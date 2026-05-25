package edu.upn.clinica.backend.horario.repository;

import edu.upn.clinica.backend.horario.model.HorarioAtencion;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class HorarioRepository extends BaseRepository {

    public List<HorarioAtencion> findAll() {
        String sql = "SELECT id_horario, id_especialidad, dia_semana, hora_inicio, hora_fin "
                + "FROM horarios_atencion ORDER BY id_especialidad, FIELD(dia_semana, "
                + "'LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO'), hora_inicio";
        List<HorarioAtencion> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (Exception e) {
            throw new RuntimeException("Error listando horarios: " + e.getMessage());
        }
        return lista;
    }

    public List<HorarioAtencion> findByEspecialidad(Integer idEspecialidad) {
        String sql = "SELECT id_horario, id_especialidad, dia_semana, hora_inicio, hora_fin "
                + "FROM horarios_atencion WHERE id_especialidad = ? ORDER BY FIELD(dia_semana, "
                + "'LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO'), hora_inicio";
        List<HorarioAtencion> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEspecialidad);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando horarios: " + e.getMessage());
        }
        return lista;
    }

    public Optional<HorarioAtencion> findById(Integer id) {
        String sql = "SELECT id_horario, id_especialidad, dia_semana, hora_inicio, hora_fin "
                + "FROM horarios_atencion WHERE id_horario = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando horario: " + e.getMessage());
        }
        return Optional.empty();
    }

    public HorarioAtencion save(HorarioAtencion h) {
        String sql = "INSERT INTO horarios_atencion (id_especialidad, dia_semana, hora_inicio, hora_fin) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, h.getIdEspecialidad());
            ps.setString(2, h.getDiaSemana());
            ps.setTime(3, Time.valueOf(h.getHoraInicio()));
            ps.setTime(4, Time.valueOf(h.getHoraFin()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) h.setIdHorario(rs.getInt(1));
            }
            return h;
        } catch (Exception e) {
            throw new RuntimeException("Error creando horario: " + e.getMessage());
        }
    }

    public void update(Integer id, Integer idEspecialidad, String diaSemana,
                        String horaInicio, String horaFin) {
        String sql = "UPDATE horarios_atencion SET id_especialidad = ?, dia_semana = ?, "
                + "hora_inicio = ?, hora_fin = ? WHERE id_horario = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEspecialidad);
            ps.setString(2, diaSemana);
            ps.setTime(3, Time.valueOf(horaInicio));
            ps.setTime(4, Time.valueOf(horaFin));
            ps.setInt(5, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando horario: " + e.getMessage());
        }
    }

    public void delete(Integer id) {
        String sql = "DELETE FROM horarios_atencion WHERE id_horario = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando horario: " + e.getMessage());
        }
    }

    private HorarioAtencion mapRow(ResultSet rs) throws Exception {
        HorarioAtencion h = new HorarioAtencion();
        h.setIdHorario(rs.getInt("id_horario"));
        h.setIdEspecialidad(rs.getInt("id_especialidad"));
        h.setDiaSemana(rs.getString("dia_semana"));
        h.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
        h.setHoraFin(rs.getTime("hora_fin").toLocalTime());
        return h;
    }
}

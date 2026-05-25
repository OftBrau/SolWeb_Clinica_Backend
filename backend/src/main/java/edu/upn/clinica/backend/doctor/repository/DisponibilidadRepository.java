package edu.upn.clinica.backend.doctor.repository;

import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DisponibilidadRepository extends BaseRepository {

    public List<DisponibilidadRow> findByDoctor(Integer idDoctor) {
        String sql = "SELECT id_disponibilidad, id_doctor, dia_semana, hora_inicio, hora_fin, activo " +
                     "FROM disponibilidad_doctor WHERE id_doctor = ? ORDER BY FIELD(dia_semana, 'LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO'), hora_inicio";
        List<DisponibilidadRow> lista = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error consultando disponibilidad: " + e.getMessage());
        }
        return lista;
    }

    public DisponibilidadRow save(DisponibilidadRow row) {
        String sql = "INSERT INTO disponibilidad_doctor (id_doctor, dia_semana, hora_inicio, hora_fin, activo) VALUES (?, ?, ?, ?, 1)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, row.getIdDoctor());
            ps.setString(2, row.getDiaSemana());
            ps.setTime(3, Time.valueOf(row.getHoraInicio()));
            ps.setTime(4, Time.valueOf(row.getHoraFin()));
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) row.setIdDisponibilidad(rs.getInt(1));
            return row;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando disponibilidad: " + e.getMessage());
        }
    }

    public void update(DisponibilidadRow row) {
        String sql = "UPDATE disponibilidad_doctor SET dia_semana = ?, hora_inicio = ?, hora_fin = ? WHERE id_disponibilidad = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, row.getDiaSemana());
            ps.setTime(2, Time.valueOf(row.getHoraInicio()));
            ps.setTime(3, Time.valueOf(row.getHoraFin()));
            ps.setInt(4, row.getIdDisponibilidad());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando disponibilidad: " + e.getMessage());
        }
    }

    public void delete(Integer idDisponibilidad) {
        String sql = "DELETE FROM disponibilidad_doctor WHERE id_disponibilidad = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDisponibilidad);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando disponibilidad: " + e.getMessage());
        }
    }

    public Optional<DisponibilidadRow> findById(Integer id) {
        String sql = "SELECT id_disponibilidad, id_doctor, dia_semana, hora_inicio, hora_fin, activo FROM disponibilidad_doctor WHERE id_disponibilidad = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando disponibilidad: " + e.getMessage());
        }
        return Optional.empty();
    }

    public boolean existeConflicto(Integer idDoctor, String diaSemana, String horaInicio, String horaFin) {
        String sql = "SELECT COUNT(*) FROM disponibilidad_doctor " +
                     "WHERE id_doctor = ? AND dia_semana = ? AND activo = 1 " +
                     "AND ((hora_inicio <= ? AND hora_fin > ?) OR (hora_inicio < ? AND hora_fin >= ?)) ";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            ps.setString(2, diaSemana);
            ps.setTime(3, Time.valueOf(horaInicio));
            ps.setTime(4, Time.valueOf(horaInicio));
            ps.setTime(5, Time.valueOf(horaFin));
            ps.setTime(6, Time.valueOf(horaFin));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error verificando conflicto: " + e.getMessage());
        }
    }

    private DisponibilidadRow mapRow(ResultSet rs) throws Exception {
        DisponibilidadRow row = new DisponibilidadRow();
        row.setIdDisponibilidad(rs.getInt("id_disponibilidad"));
        row.setIdDoctor(rs.getInt("id_doctor"));
        row.setDiaSemana(rs.getString("dia_semana"));
        row.setHoraInicio(rs.getTime("hora_inicio").toLocalTime().toString());
        row.setHoraFin(rs.getTime("hora_fin").toLocalTime().toString());
        row.setActivo(rs.getInt("activo"));
        return row;
    }

    public static class DisponibilidadRow {
        private Integer idDisponibilidad;
        private Integer idDoctor;
        private String diaSemana;
        private String horaInicio;
        private String horaFin;
        private Integer activo;

        public Integer getIdDisponibilidad() { return idDisponibilidad; }
        public void setIdDisponibilidad(Integer v) { this.idDisponibilidad = v; }

        public Integer getIdDoctor() { return idDoctor; }
        public void setIdDoctor(Integer v) { this.idDoctor = v; }

        public String getDiaSemana() { return diaSemana; }
        public void setDiaSemana(String v) { this.diaSemana = v; }

        public String getHoraInicio() { return horaInicio; }
        public void setHoraInicio(String v) { this.horaInicio = v; }

        public String getHoraFin() { return horaFin; }
        public void setHoraFin(String v) { this.horaFin = v; }

        public Integer getActivo() { return activo; }
        public void setActivo(Integer v) { this.activo = v; }
    }
}

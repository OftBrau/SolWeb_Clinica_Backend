package edu.upn.clinica.backend.enfermero.repository;

import edu.upn.clinica.backend.enfermero.model.AsignacionEnfermero;
import edu.upn.clinica.backend.enfermero.model.Triaje;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EnfermeroRepository extends BaseRepository {

    public List<AsignacionEnfermero> findByDoctor(Integer idDoctor) {
        String sql = "SELECT ae.id_asignacion, ae.id_enfermero, ae.id_doctor, ae.activo, ae.fecha_asignacion, " +
                "ue.nombre AS nombre_enfermero, ue.apellido AS apellido_enfermero, ue.email AS email_enfermero, " +
                "ud.nombre AS nombre_doctor, ud.apellido AS apellido_doctor, d.especialidad AS especialidad_doctor " +
                "FROM asignaciones_enfermero ae " +
                "JOIN doctores de ON ae.id_enfermero = de.id_doctor " +
                "JOIN usuarios ue ON de.id_usuario = ue.id_usuario " +
                "JOIN doctores d ON ae.id_doctor = d.id_doctor " +
                "JOIN usuarios ud ON d.id_usuario = ud.id_usuario " +
                "WHERE ae.id_doctor = ? AND ae.activo = 1 AND ue.estado = 'ACTIVO' " +
                "ORDER BY ue.nombre";

        List<AsignacionEnfermero> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDoctor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapAsignacion(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando enfermeros por doctor: " + e.getMessage());
        }
        return lista;
    }

    public List<AsignacionEnfermero> findEnfermerosDisponibles() {
        String sql = "SELECT d.id_doctor AS id_enfermero, u.nombre, u.apellido, u.email, d.especialidad " +
                "FROM doctores d " +
                "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
                "WHERE u.rol = 'ENFERMERO' AND u.estado = 'ACTIVO' " +
                "AND d.id_doctor NOT IN (SELECT id_enfermero FROM asignaciones_enfermero WHERE activo = 1) " +
                "ORDER BY u.nombre, u.apellido";
        List<AsignacionEnfermero> lista = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AsignacionEnfermero ae = new AsignacionEnfermero();
                    ae.setIdEnfermero(rs.getInt("id_enfermero"));
                    ae.setNombreEnfermero(rs.getString("nombre"));
                    ae.setApellidoEnfermero(rs.getString("apellido"));
                    ae.setEmailEnfermero(rs.getString("email"));
                    ae.setEspecialidadDoctor(rs.getString("especialidad"));
                    lista.add(ae);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando enfermeros disponibles: " + e.getMessage());
        }
        return lista;
    }

    public void asignar(Integer idEnfermero, Integer idDoctor) {
        String sql = "INSERT INTO asignaciones_enfermero (id_enfermero, id_doctor) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEnfermero);
            ps.setInt(2, idDoctor);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error asignando enfermero: " + e.getMessage());
        }
    }

    public void desasignar(Integer idAsignacion) {
        String sql = "UPDATE asignaciones_enfermero SET activo = 0 WHERE id_asignacion = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAsignacion);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error desasignando enfermero: " + e.getMessage());
        }
    }

    public Optional<Integer> findDoctorByEnfermero(Integer idEnfermero) {
        String sql = "SELECT id_doctor FROM asignaciones_enfermero WHERE id_enfermero = ? AND activo = 1 LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEnfermero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt("id_doctor"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando doctor de enfermero: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Optional<Integer> findEnfermeroIdByEmail(String email) {
        String sql = "SELECT d.id_doctor FROM doctores d " +
                "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
                "WHERE u.email = ? AND u.rol = 'ENFERMERO' AND u.estado = 'ACTIVO'";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt(1));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando enfermero: " + e.getMessage());
        }
        return Optional.empty();
    }

    public Triaje saveTriaje(Triaje triaje) {
        String sql = "INSERT INTO triajes (id_cita, id_enfermero, presion_arterial, temperatura, " +
                "frecuencia_cardiaca, saturacion, peso, talla, motivo_consulta, notas) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, triaje.getIdCita());
            ps.setInt(2, triaje.getIdEnfermero());
            ps.setString(3, triaje.getPresionArterial());
            if (triaje.getTemperatura() != null) ps.setDouble(4, triaje.getTemperatura());
            else ps.setNull(4, Types.DOUBLE);
            if (triaje.getFrecuenciaCardiaca() != null) ps.setInt(5, triaje.getFrecuenciaCardiaca());
            else ps.setNull(5, Types.INTEGER);
            if (triaje.getSaturacion() != null) ps.setDouble(6, triaje.getSaturacion());
            else ps.setNull(6, Types.DOUBLE);
            if (triaje.getPeso() != null) ps.setDouble(7, triaje.getPeso());
            else ps.setNull(7, Types.DOUBLE);
            if (triaje.getTalla() != null) ps.setDouble(8, triaje.getTalla());
            else ps.setNull(8, Types.DOUBLE);
            ps.setString(9, triaje.getMotivoConsulta());
            ps.setString(10, triaje.getNotas());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) triaje.setIdTriaje(rs.getInt(1));
            }
            return triaje;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando triaje: " + e.getMessage());
        }
    }

    public Optional<Triaje> findTriajeByCita(Integer idCita) {
        String sql = "SELECT t.id_triaje, t.id_cita, t.id_enfermero, t.presion_arterial, t.temperatura, " +
                "t.frecuencia_cardiaca, t.saturacion, t.peso, t.talla, t.motivo_consulta, t.notas, t.created_at, " +
                "CONCAT(u.nombre, ' ', u.apellido) AS nombre_enfermero " +
                "FROM triajes t " +
                "JOIN doctores d ON t.id_enfermero = d.id_doctor " +
                "JOIN usuarios u ON d.id_usuario = u.id_usuario " +
                "WHERE t.id_cita = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapTriaje(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando triaje: " + e.getMessage());
        }
        return Optional.empty();
    }

    public void updateTriaje(Triaje triaje) {
        String sql = "UPDATE triajes SET presion_arterial = ?, temperatura = ?, frecuencia_cardiaca = ?, " +
                "saturacion = ?, peso = ?, talla = ?, motivo_consulta = ?, notas = ? WHERE id_cita = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, triaje.getPresionArterial());
            if (triaje.getTemperatura() != null) ps.setDouble(2, triaje.getTemperatura());
            else ps.setNull(2, Types.DOUBLE);
            if (triaje.getFrecuenciaCardiaca() != null) ps.setInt(3, triaje.getFrecuenciaCardiaca());
            else ps.setNull(3, Types.INTEGER);
            if (triaje.getSaturacion() != null) ps.setDouble(4, triaje.getSaturacion());
            else ps.setNull(4, Types.DOUBLE);
            if (triaje.getPeso() != null) ps.setDouble(5, triaje.getPeso());
            else ps.setNull(5, Types.DOUBLE);
            if (triaje.getTalla() != null) ps.setDouble(6, triaje.getTalla());
            else ps.setNull(6, Types.DOUBLE);
            ps.setString(7, triaje.getMotivoConsulta());
            ps.setString(8, triaje.getNotas());
            ps.setInt(9, triaje.getIdCita());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando triaje: " + e.getMessage());
        }
    }

    private AsignacionEnfermero mapAsignacion(ResultSet rs) throws Exception {
        AsignacionEnfermero ae = new AsignacionEnfermero();
        ae.setIdAsignacion(rs.getInt("id_asignacion"));
        ae.setIdEnfermero(rs.getInt("id_enfermero"));
        ae.setIdDoctor(rs.getInt("id_doctor"));
        ae.setActivo(rs.getBoolean("activo"));
        Timestamp fa = rs.getTimestamp("fecha_asignacion");
        if (fa != null) ae.setFechaAsignacion(fa.toLocalDateTime());
        ae.setNombreEnfermero(rs.getString("nombre_enfermero"));
        ae.setApellidoEnfermero(rs.getString("apellido_enfermero"));
        ae.setEmailEnfermero(rs.getString("email_enfermero"));
        ae.setNombreDoctor(rs.getString("nombre_doctor"));
        ae.setApellidoDoctor(rs.getString("apellido_doctor"));
        ae.setEspecialidadDoctor(rs.getString("especialidad_doctor"));
        return ae;
    }

    private Triaje mapTriaje(ResultSet rs) throws Exception {
        Triaje t = new Triaje();
        t.setIdTriaje(rs.getInt("id_triaje"));
        t.setIdCita(rs.getInt("id_cita"));
        t.setIdEnfermero(rs.getInt("id_enfermero"));
        t.setPresionArterial(rs.getString("presion_arterial"));
        double temp = rs.getDouble("temperatura");
        if (!rs.wasNull()) t.setTemperatura(temp);
        int fc = rs.getInt("frecuencia_cardiaca");
        if (!rs.wasNull()) t.setFrecuenciaCardiaca(fc);
        double sat = rs.getDouble("saturacion");
        if (!rs.wasNull()) t.setSaturacion(sat);
        double peso = rs.getDouble("peso");
        if (!rs.wasNull()) t.setPeso(peso);
        double talla = rs.getDouble("talla");
        if (!rs.wasNull()) t.setTalla(talla);
        t.setMotivoConsulta(rs.getString("motivo_consulta"));
        t.setNotas(rs.getString("notas"));
        Timestamp cat = rs.getTimestamp("created_at");
        if (cat != null) t.setCreatedAt(cat.toLocalDateTime());
        return t;
    }
}

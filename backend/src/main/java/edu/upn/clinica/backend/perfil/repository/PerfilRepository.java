package edu.upn.clinica.backend.perfil.repository;

import edu.upn.clinica.backend.perfil.model.PerfilProfesional;
import edu.upn.clinica.backend.shared.BaseRepository;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PerfilRepository extends BaseRepository {

    public Optional<PerfilProfesional> findByPracticante(Integer idPracticante) {
        String sql = "SELECT pp.*, CONCAT(u.nombre,' ',u.apellido) AS nombre_completo, u.email, u.foto_url " +
                "FROM perfil_profesional pp " +
                "JOIN practicantes p ON pp.id_practicante = p.id_practicante " +
                "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "WHERE pp.id_practicante = ? AND pp.activo = TRUE ORDER BY pp.id_perfil DESC LIMIT 1";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPracticante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return Optional.empty();
    }

    public PerfilProfesional save(PerfilProfesional p) {
        String sql = "INSERT INTO perfil_profesional (id_practicante, titulo_profesional, universidad, anio_graduacion, biografia, linkedin_url, banner_url, cv_url) " +
                "VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " +
                "titulo_profesional=VALUES(titulo_profesional), universidad=VALUES(universidad), " +
                "anio_graduacion=VALUES(anio_graduacion), biografia=VALUES(biografia), linkedin_url=VALUES(linkedin_url), " +
                "banner_url=VALUES(banner_url), cv_url=VALUES(cv_url)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdPracticante());
            ps.setString(2, p.getTituloProfesional());
            ps.setString(3, p.getUniversidad());
            ps.setObject(4, p.getAnioGraduacion(), Types.INTEGER);
            ps.setString(5, p.getBiografia());
            ps.setString(6, p.getLinkedinUrl());
            ps.setString(7, p.getBannerUrl());
            ps.setString(8, p.getCvUrl());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setIdPerfil(keys.getInt(1));
            }
            return p;
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    // --- Certificaciones ---
    public Map<String, Object> saveCertificacion(Integer idPerfil, String nombre, String institucion, String fechaEmision, String fechaVencimiento, String archivoUrl) {
        String sql = "INSERT INTO certificaciones (id_perfil, nombre, institucion, fecha_emision, fecha_vencimiento, archivo_url) VALUES (?,?,?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPerfil); ps.setString(2, nombre); ps.setString(3, institucion);
            ps.setDate(4, fechaEmision != null ? java.sql.Date.valueOf(fechaEmision) : null);
            ps.setDate(5, fechaVencimiento != null ? java.sql.Date.valueOf(fechaVencimiento) : null);
            ps.setString(6, archivoUrl);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return Map.of("id", keys.getInt(1));
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return Map.of();
    }

    public List<Map<String, Object>> findCertificaciones(Integer idPerfil) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM certificaciones WHERE id_perfil = ? ORDER BY fecha_emision DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPerfil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", rs.getInt("id_certificacion")); m.put("nombre", rs.getString("nombre"));
                    m.put("institucion", rs.getString("institucion"));
                    Date fe = rs.getDate("fecha_emision"); if (fe != null) m.put("fechaEmision", fe.toString());
                    Date fv = rs.getDate("fecha_vencimiento"); if (fv != null) m.put("fechaVencimiento", fv.toString());
                    m.put("archivoUrl", rs.getString("archivo_url"));
                    list.add(m);
                }
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    public void deleteCertificacion(Integer id) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM certificaciones WHERE id_certificacion = ?")) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    // --- Experiencia ---
    public Map<String, Object> saveExperiencia(Integer idPerfil, String empresa, String cargo, String fechaInicio, String fechaFin, Boolean actualmente, String descripcion) {
        String sql = "INSERT INTO experiencia_laboral (id_perfil, empresa, cargo, fecha_inicio, fecha_fin, actualmente, descripcion) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPerfil); ps.setString(2, empresa); ps.setString(3, cargo);
            ps.setDate(4, fechaInicio != null ? java.sql.Date.valueOf(fechaInicio) : null);
            ps.setDate(5, fechaFin != null ? java.sql.Date.valueOf(fechaFin) : null);
            ps.setBoolean(6, actualmente != null ? actualmente : false);
            ps.setString(7, descripcion);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) return Map.of("id", keys.getInt(1)); }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return Map.of();
    }

    public List<Map<String, Object>> findExperiencias(Integer idPerfil) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM experiencia_laboral WHERE id_perfil = ? ORDER BY fecha_inicio DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPerfil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", rs.getInt("id_experiencia")); m.put("empresa", rs.getString("empresa"));
                    m.put("cargo", rs.getString("cargo"));
                    Date fi = rs.getDate("fecha_inicio"); if (fi != null) m.put("fechaInicio", fi.toString());
                    Date ff = rs.getDate("fecha_fin"); if (ff != null) m.put("fechaFin", ff.toString());
                    m.put("actualmente", rs.getBoolean("actualmente")); m.put("descripcion", rs.getString("descripcion"));
                    list.add(m);
                }
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    public void deleteExperiencia(Integer id) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM experiencia_laboral WHERE id_experiencia = ?")) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    // --- Educacion ---
    public Map<String, Object> saveEducacion(Integer idPerfil, String institucion, String titulo, String fechaInicio, String fechaFin) {
        String sql = "INSERT INTO educacion (id_perfil, institucion, titulo, fecha_inicio, fecha_fin) VALUES (?,?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPerfil); ps.setString(2, institucion); ps.setString(3, titulo);
            ps.setDate(4, fechaInicio != null ? java.sql.Date.valueOf(fechaInicio) : null);
            ps.setDate(5, fechaFin != null ? java.sql.Date.valueOf(fechaFin) : null);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) return Map.of("id", keys.getInt(1)); }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return Map.of();
    }

    public List<Map<String, Object>> findEducaciones(Integer idPerfil) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM educacion WHERE id_perfil = ? ORDER BY fecha_inicio DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idPerfil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", rs.getInt("id_educacion")); m.put("institucion", rs.getString("institucion"));
                    m.put("titulo", rs.getString("titulo"));
                    Date fi = rs.getDate("fecha_inicio"); if (fi != null) m.put("fechaInicio", fi.toString());
                    Date ff = rs.getDate("fecha_fin"); if (ff != null) m.put("fechaFin", ff.toString());
                    list.add(m);
                }
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    public void deleteEducacion(Integer id) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM educacion WHERE id_educacion = ?")) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    // --- Habilidades ---
    public Map<String, Object> saveHabilidad(Integer idPerfil, String nombre) {
        String sql = "INSERT INTO habilidades (id_perfil, nombre) VALUES (?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idPerfil); ps.setString(2, nombre);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) return Map.of("id", keys.getInt(1)); }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return Map.of();
    }

    public List<Map<String, Object>> findHabilidades(Integer idPerfil) {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM habilidades WHERE id_perfil = ?")) {
            ps.setInt(1, idPerfil);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(Map.of("id", rs.getInt("id_habilidad"), "nombre", rs.getString("nombre")));
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    public void deleteHabilidad(Integer id) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM habilidades WHERE id_habilidad = ?")) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    // --- Doctor view: all active profiles ---
    public List<PerfilProfesional> findAllActivos() {
        List<PerfilProfesional> list = new ArrayList<>();
        String sql = "SELECT pp.*, CONCAT(u.nombre,' ',u.apellido) AS nombre_completo, u.email, u.foto_url " +
                "FROM perfil_profesional pp " +
                "JOIN practicantes p ON pp.id_practicante = p.id_practicante " +
                "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "WHERE pp.activo = TRUE ORDER BY pp.id_perfil DESC";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    private PerfilProfesional mapRow(ResultSet rs) throws Exception {
        PerfilProfesional p = new PerfilProfesional();
        p.setIdPerfil(rs.getInt("id_perfil"));
        p.setIdPracticante(rs.getInt("id_practicante"));
        p.setTituloProfesional(rs.getString("titulo_profesional"));
        p.setUniversidad(rs.getString("universidad"));
        p.setAnioGraduacion(rs.getObject("anio_graduacion") != null ? rs.getInt("anio_graduacion") : null);
        p.setBiografia(rs.getString("biografia"));
        p.setLinkedinUrl(rs.getString("linkedin_url"));
        p.setActivo(rs.getBoolean("activo"));
        p.setNombreCompleto(rs.getString("nombre_completo"));
        p.setEmail(rs.getString("email"));
        p.setFotoUrl(rs.getString("foto_url"));
        p.setBannerUrl(rs.getString("banner_url"));
        p.setCvUrl(rs.getString("cv_url"));
        return p;
    }

    public List<String> buscarUniversidades(String query) {
        List<String> list = new ArrayList<>();
        String sql = "SELECT nombre FROM universidades WHERE nombre LIKE ? ORDER BY nombre LIMIT 10";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rs.getString("nombre"));
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return list;
    }

    public void saveUniversidad(String nombre) {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement("INSERT IGNORE INTO universidades (nombre) VALUES (?)")) {
            ps.setString(1, nombre);
            ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    public void saveBanner(Integer idPracticante, String url) {
        String sql = "UPDATE perfil_profesional SET banner_url = ? WHERE id_practicante = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, url); ps.setInt(2, idPracticante);
            ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }

    public void saveCv(Integer idPracticante, String url) {
        String sql = "UPDATE perfil_profesional SET cv_url = ? WHERE id_practicante = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, url); ps.setInt(2, idPracticante);
            ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
    }
}

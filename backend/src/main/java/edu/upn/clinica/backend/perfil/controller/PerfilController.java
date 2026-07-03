package edu.upn.clinica.backend.perfil.controller;

import edu.upn.clinica.backend.doctor.service.CloudinaryService;
import edu.upn.clinica.backend.perfil.repository.PerfilRepository;
import edu.upn.clinica.backend.perfil.service.PerfilService;
import edu.upn.clinica.backend.perfil.service.CvParserService;
import edu.upn.clinica.backend.practicante.repository.PracticanteRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    @Autowired private PerfilService perfilService;
    @Autowired private PerfilRepository perfilRepository;
    @Autowired private PracticanteRepository practicanteRepository;
    @Autowired private CloudinaryService cloudinaryService;
    @Autowired private DataSource dataSource;

    // --- Profile photo for any user ---
    @PostMapping("/foto")
    public ResponseEntity<ApiResponse<Map<String, String>>> subirFoto(
            Authentication auth, @RequestParam("file") MultipartFile file) {
        String email = auth.getName();
        String url = cloudinaryService.subirFoto(file);
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE usuarios SET foto_url = ? WHERE email = ?")) {
            ps.setString(1, url);
            ps.setString(2, email);
            ps.executeUpdate();
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        return ResponseEntity.ok(ApiResponse.ok("Foto actualizada", Map.of("url", url)));
    }

    // --- Professional profile (practicante only) ---
    @GetMapping("/profesional")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPerfil(Authentication auth) {
        Integer idPracticante = getPracticanteId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Perfil", perfilService.getOrCreate(idPracticante)));
    }

    @PutMapping("/profesional")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updatePerfil(Authentication auth, @RequestBody Map<String, Object> body) {
        Integer idPracticante = getPracticanteId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Perfil actualizado", perfilService.updatePerfil(idPracticante, body)));
    }

    @PostMapping("/profesional/certificaciones")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addCert(Authentication auth, @RequestBody Map<String, String> body) {
        Integer idPerfil = perfilService.getPerfil(getPracticanteId(auth)).get("perfil") != null ?
                ((edu.upn.clinica.backend.perfil.model.PerfilProfesional) perfilService.getPerfil(getPracticanteId(auth)).get("perfil")).getIdPerfil() : null;
        if (idPerfil == null) throw new AppException("Crea tu perfil primero", HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(ApiResponse.ok("Certificacion agregada", perfilService.addCertificacion(idPerfil, body)));
    }

    @PostMapping("/profesional/experiencia")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addExp(Authentication auth, @RequestBody Map<String, Object> body) {
        Integer idPerfil = getPerfilId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Experiencia agregada", perfilService.addExperiencia(idPerfil, body)));
    }

    @PostMapping("/profesional/educacion")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addEdu(Authentication auth, @RequestBody Map<String, String> body) {
        Integer idPerfil = getPerfilId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Educacion agregada", perfilService.addEducacion(idPerfil, body)));
    }

    @PostMapping("/profesional/habilidades")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addHab(Authentication auth, @RequestBody Map<String, String> body) {
        Integer idPerfil = getPerfilId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Habilidad agregada", perfilService.addHabilidad(idPerfil, body)));
    }

    @DeleteMapping("/profesional/certificaciones/{id}")
    public ResponseEntity<ApiResponse<Void>> delCert(@PathVariable Integer id) {
        perfilRepository.deleteCertificacion(id);
        return ResponseEntity.ok(ApiResponse.ok("Eliminado"));
    }

    @DeleteMapping("/profesional/experiencia/{id}")
    public ResponseEntity<ApiResponse<Void>> delExp(@PathVariable Integer id) {
        perfilRepository.deleteExperiencia(id);
        return ResponseEntity.ok(ApiResponse.ok("Eliminado"));
    }

    @DeleteMapping("/profesional/educacion/{id}")
    public ResponseEntity<ApiResponse<Void>> delEdu(@PathVariable Integer id) {
        perfilRepository.deleteEducacion(id);
        return ResponseEntity.ok(ApiResponse.ok("Eliminado"));
    }

    @DeleteMapping("/profesional/habilidades/{id}")
    public ResponseEntity<ApiResponse<Void>> delHab(@PathVariable Integer id) {
        perfilRepository.deleteHabilidad(id);
        return ResponseEntity.ok(ApiResponse.ok("Eliminado"));
    }

    // --- Doctor view ---
    @GetMapping("/practicantes")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDoctorView() {
        return ResponseEntity.ok(ApiResponse.ok("Practicantes", perfilService.getDoctorView()));
    }

    @Autowired private CvParserService cvParserService;

    @PostMapping("/parsear-cv")
    public ResponseEntity<ApiResponse<Map<String, Object>>> parsearCv(@RequestBody Map<String, String> body) {
        String url = body.get("url");
        if (url == null || url.isBlank()) throw new AppException("URL requerida", HttpStatus.BAD_REQUEST);
        return ResponseEntity.ok(ApiResponse.ok("CV analizado", cvParserService.parsear(url)));
    }
    @PostMapping("/banner")
    public ResponseEntity<ApiResponse<Map<String, String>>> subirBanner(Authentication auth, @RequestParam("file") MultipartFile file) {
        Integer idPracticante = getPracticanteId(auth);
        String url = cloudinaryService.subirArchivo(file, "banners");
        perfilRepository.saveBanner(idPracticante, url);
        return ResponseEntity.ok(ApiResponse.ok("Banner actualizado", Map.of("url", url)));
    }

    @PostMapping("/cv")
    public ResponseEntity<ApiResponse<Map<String, String>>> subirCv(Authentication auth, @RequestParam("file") MultipartFile file) {
        Integer idPracticante = getPracticanteId(auth);
        String url = cloudinaryService.subirArchivo(file, "cvs");
        perfilRepository.saveCv(idPracticante, url);
        return ResponseEntity.ok(ApiResponse.ok("CV subido", Map.of("url", url)));
    }

    @GetMapping("/universidades")
    public ResponseEntity<ApiResponse<List<String>>> buscarUniversidades(@RequestParam String q) {
        return ResponseEntity.ok(ApiResponse.ok("Universidades", perfilRepository.buscarUniversidades(q)));
    }

    private Integer getPracticanteId(Authentication auth) {
        String email = auth.getName();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                "SELECT p.id_practicante FROM practicantes p JOIN usuarios u ON p.id_usuario = u.id_usuario WHERE u.email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_practicante");
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        throw new AppException("Solo practicantes pueden acceder", HttpStatus.FORBIDDEN);
    }

    private Integer getPerfilId(Authentication auth) {
        Map<String, Object> data = perfilService.getPerfil(getPracticanteId(auth));
        Object p = data.get("perfil");
        if (p instanceof edu.upn.clinica.backend.perfil.model.PerfilProfesional pp) return pp.getIdPerfil();
        throw new AppException("Perfil no encontrado", HttpStatus.NOT_FOUND);
    }
}

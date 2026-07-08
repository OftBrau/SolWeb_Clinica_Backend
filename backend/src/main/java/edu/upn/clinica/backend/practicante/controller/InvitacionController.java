package edu.upn.clinica.backend.practicante.controller;

import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.practicante.repository.PracticanteRepository;
import edu.upn.clinica.backend.shared.ApiResponse;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.teleconsulta.notificacion.NotificacionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/practicante")
public class InvitacionController {

    @Autowired private PracticanteRepository repo;
    @Autowired private DoctorRepository doctorRepo;
    @Autowired private DataSource dataSource;
    @Autowired private SimpMessagingTemplate messaging;

    @PostMapping("/invitaciones")
    public ResponseEntity<ApiResponse<Map<String, Object>>> invitar(Authentication auth, @RequestBody Map<String, Object> body) {
        Integer idDoctor = getDoctorId(auth);
        Integer idPracticante = ((Number) body.get("idPracticante")).intValue();
        String mensaje = (String) body.getOrDefault("mensaje", "");

        Map<String, Object> r = repo.saveInvitacion(idDoctor, idPracticante, mensaje);

        // Notificar al practicante
        String emailPracticante = getEmailPracticante(idPracticante);
        if (emailPracticante != null) {
            String nombreDoctor = doctorRepo.findById(idDoctor).map(d -> d.getNombre()).orElse("Un doctor");
            messaging.convertAndSend("/topic/notificaciones/paciente/" + emailPracticante,
                    new NotificacionDTO("INVITACION_PRACTICA",
                            nombreDoctor + " te ha invitado a prácticas", (Integer) r.get("id")));
        }
        return ResponseEntity.ok(ApiResponse.ok("Invitación enviada", r));
    }

    @GetMapping("/invitaciones/doctor")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> invitacionesEnviadas(Authentication auth) {
        return ResponseEntity.ok(ApiResponse.ok("Invitaciones enviadas", repo.findInvitacionesByDoctor(getDoctorId(auth))));
    }

    @GetMapping("/mis-invitaciones")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> misInvitaciones(Authentication auth) {
        Integer idPracticante = getPracticanteId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Invitaciones recibidas", repo.findInvitacionesByPracticante(idPracticante)));
    }

    @PutMapping("/invitaciones/{id}/aceptar")
    public ResponseEntity<ApiResponse<Void>> aceptar(Authentication auth, @PathVariable Integer id) {
        Map<String, Object> inv = repo.findInvitacionById(id);
        if (inv == null) throw new AppException("Invitación no encontrada", HttpStatus.NOT_FOUND);

        Integer idPracticante = getPracticanteId(auth);
        if (!inv.get("idPracticante").equals(idPracticante))
            throw new AppException("No autorizado", HttpStatus.FORBIDDEN);

        repo.updateInvitacionEstado(id, "ACEPTADA");
        repo.asignarSupervision((Integer) inv.get("idDoctor"), idPracticante);

        // Notificar al doctor
        String emailDoctor = getEmailDoctor((Integer) inv.get("idDoctor"));
        if (emailDoctor != null) {
            String nombrePracticante = (String) inv.getOrDefault("practicante", "Un practicante");
            messaging.convertAndSend("/topic/notificaciones/paciente/" + emailDoctor,
                    new NotificacionDTO("INVITACION_ACEPTADA",
                            nombrePracticante + " aceptó tu invitación", id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Invitación aceptada"));
    }

    @PutMapping("/invitaciones/{id}/rechazar")
    public ResponseEntity<ApiResponse<Void>> rechazar(Authentication auth, @PathVariable Integer id) {
        Map<String, Object> inv = repo.findInvitacionById(id);
        if (inv == null) throw new AppException("Invitación no encontrada", HttpStatus.NOT_FOUND);

        Integer idPracticante = getPracticanteId(auth);
        if (!inv.get("idPracticante").equals(idPracticante))
            throw new AppException("No autorizado", HttpStatus.FORBIDDEN);

        repo.updateInvitacionEstado(id, "RECHAZADA");

        String emailDoctor = getEmailDoctor((Integer) inv.get("idDoctor"));
        if (emailDoctor != null) {
            String nombrePracticante = (String) inv.getOrDefault("practicante", "Un practicante");
            messaging.convertAndSend("/topic/notificaciones/paciente/" + emailDoctor,
                    new NotificacionDTO("INVITACION_RECHAZADA",
                            nombrePracticante + " rechazó tu invitación", id));
        }
        return ResponseEntity.ok(ApiResponse.ok("Invitación rechazada"));
    }

    private Integer getDoctorId(Authentication auth) {
        String email = auth.getName();
        return doctorRepo.findIdByEmail(email)
                .orElseThrow(() -> new AppException("Doctor no encontrado", HttpStatus.FORBIDDEN));
    }

    private Integer getPracticanteId(Authentication auth) {
        String email = auth.getName();
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(
                "SELECT dp.id_doctor AS id_practicante FROM doctores dp JOIN usuarios u ON dp.id_usuario = u.id_usuario WHERE u.email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id_practicante");
            }
        } catch (Exception e) { throw new RuntimeException(e.getMessage()); }
        throw new AppException("Solo practicantes pueden acceder", HttpStatus.FORBIDDEN);
    }

    private String getEmailPracticante(Integer idPracticante) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT u.email FROM doctores dp JOIN usuarios u ON dp.id_usuario = u.id_usuario WHERE dp.id_doctor = ?")) {
            ps.setInt(1, idPracticante);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getString("email"); }
        } catch (Exception e) {}
        return null;
    }

    private String getEmailDoctor(Integer idDoctor) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT u.email FROM doctores d JOIN usuarios u ON d.id_usuario = u.id_usuario WHERE d.id_doctor = ?")) {
            ps.setInt(1, idDoctor);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getString("email"); }
        } catch (Exception e) {}
        return null;
    }
}

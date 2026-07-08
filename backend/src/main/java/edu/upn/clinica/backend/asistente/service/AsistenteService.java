package edu.upn.clinica.backend.asistente.service;

import edu.upn.clinica.backend.asistente.dto.AgendaDiariaResponse;
import edu.upn.clinica.backend.asistente.model.CitaPendiente;
import edu.upn.clinica.backend.asistente.repository.AsistenteRepository;
import edu.upn.clinica.backend.cita.repository.CitaRepository;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.teleconsulta.notificacion.NotificacionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class AsistenteService {

    @Autowired
    private AsistenteRepository asistenteRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private SimpMessagingTemplate messaging;

    @Autowired
    private DataSource dataSource;

    public List<CitaPendiente> listarPendientes() {
        return asistenteRepository.findCitasPendientes();
    }

    public List<Map<String, Object>> listarDoctoresDisponibles(LocalDate fecha, LocalTime hora, Integer idEspecialidad) {
        return asistenteRepository.findDoctoresDisponiblesEspecialidad(fecha, hora, idEspecialidad);
    }

    public void asignarDoctor(Integer idCita, Integer idDoctor, Integer idConsultorio, Integer idAsistente) {
        citaRepository.findById(idCita)
                .orElseThrow(() -> new AppException("Cita no encontrada", HttpStatus.NOT_FOUND));

        asistenteRepository.asignarDoctor(idCita, idDoctor, idConsultorio, idAsistente);

        String emailPaciente = obtenerEmailPacienteDeCita(idCita);
        if (emailPaciente != null) {
            messaging.convertAndSend("/topic/notificaciones/paciente/" + emailPaciente,
                    new NotificacionDTO("CITA_ASIGNADA",
                            "Tu cita con especialista ha sido asignada a un doctor. Revisa tus citas.",
                            idCita));
        }
    }

    public void rechazarCita(Integer idCita, String motivo) {
        citaRepository.findById(idCita)
                .orElseThrow(() -> new AppException("Cita no encontrada", HttpStatus.NOT_FOUND));

        asistenteRepository.rechazarCita(idCita, motivo);

        String emailPaciente = obtenerEmailPacienteDeCita(idCita);
        if (emailPaciente != null) {
            messaging.convertAndSend("/topic/notificaciones/paciente/" + emailPaciente,
                    new NotificacionDTO("CITA_RECHAZADA",
                            "Tu solicitud de cita con especialista fue rechazada: " +
                                    (motivo != null ? motivo : "Sin motivo especificado"),
                            idCita));
        }
    }

    public List<Map<String, Object>> listarAgendaDiaria(LocalDate fecha) {
        return asistenteRepository.findAgendaDiaria(fecha);
    }

    public List<Map<String, Object>> listarDoctores() {
        return asistenteRepository.findAllDoctores();
    }

    public List<Map<String, Object>> listarEspecialidadesActivas() {
        return asistenteRepository.findEspecialidadesActivas();
    }

    public List<Map<String, Object>> listarConsultoriosDisponibles(LocalDate fecha, LocalTime hora) {
        return asistenteRepository.findConsultoriosDisponibles(fecha, hora);
    }

    private String obtenerEmailPacienteDeCita(Integer idCita) {
        String sql = "SELECT u.email FROM citas c " +
                "JOIN pacientes p ON c.id_paciente = p.id_paciente " +
                "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "WHERE c.id_cita = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("email");
            }
        } catch (Exception e) {
            System.err.println("Error obteniendo email de paciente: " + e.getMessage());
        }
        return null;
    }
}

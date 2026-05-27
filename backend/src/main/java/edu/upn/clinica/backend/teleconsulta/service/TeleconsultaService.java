package edu.upn.clinica.backend.teleconsulta.service;

import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.paciente.model.Paciente;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.teleconsulta.dto.SolicitarTeleconsultaRequest;
import edu.upn.clinica.backend.teleconsulta.dto.TeleconsultaDTO;
import edu.upn.clinica.backend.teleconsulta.model.Teleconsulta;
import edu.upn.clinica.backend.teleconsulta.notificacion.NotificacionDTO;
import edu.upn.clinica.backend.teleconsulta.repository.TeleconsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class TeleconsultaService {

    @Autowired private TeleconsultaRepository teleconsultaRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private SimpMessagingTemplate messaging;

    public List<TeleconsultaDTO> listarPorPaciente(Integer idPaciente) {
        return teleconsultaRepository.findByPaciente(idPaciente)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<TeleconsultaDTO> listarPorDoctor(Integer idDoctor) {
        return teleconsultaRepository.findByDoctor(idDoctor)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<TeleconsultaDTO> listarTodas() {
        return teleconsultaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public TeleconsultaDTO solicitar(Integer idPaciente, SolicitarTeleconsultaRequest req) {
        Integer idDoctor = doctorRepository.findIdByNombreCompleto(req.getMedico())
                .orElseThrow(() -> new AppException("Doctor no encontrado: " + req.getMedico(),
                        HttpStatus.NOT_FOUND));

        Teleconsulta t = new Teleconsulta();
        t.setIdPaciente(idPaciente);
        t.setIdDoctor(idDoctor);
        t.setEspecialidad(req.getEspecialidad());
        t.setFecha(LocalDate.parse(req.getFecha()));
        t.setHora(LocalTime.parse(req.getHora()));
        t.setMotivo(req.getMotivo());
        t.setEstado("PENDIENTE");

        String uuid = UUID.randomUUID().toString();
        t.setUrlSesion("https://meet.jit.si/ClinicaUPN-" + uuid);

        Teleconsulta guardada = teleconsultaRepository.save(t);

        String nombrePac = pacienteRepository.findById(idPaciente)
                .map(p -> p.getNombre() + " " + p.getApellido())
                .orElse("Paciente");
        messaging.convertAndSend("/topic/notificaciones/doctor",
                new NotificacionDTO("NUEVA_CONSULTA",
                        "Nueva teleconsulta solicitada por " + nombrePac,
                        guardada.getIdTeleconsulta()));

        return toDTO(guardada);
    }

    public TeleconsultaDTO obtenerPorId(Integer id) {
        Teleconsulta t = teleconsultaRepository.findById(id)
                .orElseThrow(() -> new AppException("Teleconsulta no encontrada",
                        HttpStatus.NOT_FOUND));
        return toDTO(t);
    }

    public TeleconsultaDTO buscarPorCita(Integer idCita) {
        Teleconsulta t = teleconsultaRepository.findByCitaId(idCita)
                .orElseThrow(() -> new AppException("No hay teleconsulta asociada a esta cita",
                        HttpStatus.NOT_FOUND));
        return toDTO(t);
    }

    public TeleconsultaDTO aceptar(Integer id, Integer idDoctor) {
        Teleconsulta t = teleconsultaRepository.findById(id)
                .orElseThrow(() -> new AppException("Teleconsulta no encontrada",
                        HttpStatus.NOT_FOUND));

        if (!t.getIdDoctor().equals(idDoctor)) {
            throw new AppException("Esta teleconsulta no te pertenece", HttpStatus.FORBIDDEN);
        }

        if (!"PENDIENTE".equals(t.getEstado())) {
            throw new AppException("Solo se pueden aceptar teleconsultas pendientes",
                    HttpStatus.CONFLICT);
        }

        teleconsultaRepository.actualizarEstado(id, "CONFIRMADA");
        t.setEstado("CONFIRMADA");

        pacienteRepository.findById(t.getIdPaciente())
                .map(Paciente::getEmail)
                .ifPresent(email ->
                    messaging.convertAndSend("/topic/notificaciones/paciente/" + email,
                            new NotificacionDTO("CONSULTA_ACEPTADA",
                                    "Tu teleconsulta fue aceptada. ¡Conéctate!",
                                    id))
                );

        return toDTO(t);
    }

    public TeleconsultaDTO completar(Integer id, Integer idDoctor) {
        Teleconsulta t = teleconsultaRepository.findById(id)
                .orElseThrow(() -> new AppException("Teleconsulta no encontrada",
                        HttpStatus.NOT_FOUND));

        if (!t.getIdDoctor().equals(idDoctor)) {
            throw new AppException("Esta teleconsulta no te pertenece", HttpStatus.FORBIDDEN);
        }

        if (!"CONFIRMADA".equals(t.getEstado())) {
            throw new AppException("Solo se pueden completar teleconsultas confirmadas",
                    HttpStatus.CONFLICT);
        }

        teleconsultaRepository.actualizarEstado(id, "COMPLETADA");
        t.setEstado("COMPLETADA");

        pacienteRepository.findById(t.getIdPaciente())
                .map(Paciente::getEmail)
                .ifPresent(email ->
                    messaging.convertAndSend("/topic/notificaciones/paciente/" + email,
                            new NotificacionDTO("CONSULTA_COMPLETADA",
                                    "Tu teleconsulta ha sido completada",
                                    id))
                );

        return toDTO(t);
    }

    private TeleconsultaDTO toDTO(Teleconsulta t) {
        String nombrePaciente = pacienteRepository.findById(t.getIdPaciente())
                .map(p -> p.getNombre() + " " + p.getApellido())
                .orElse("Paciente #" + t.getIdPaciente());

        String nombreDoctor = "Desconocido";
        if (t.getIdDoctor() != null) {
            nombreDoctor = doctorRepository.findById(t.getIdDoctor())
                    .map(d -> d.getNombre())
                    .orElse("Doctor #" + t.getIdDoctor());
        }

        return new TeleconsultaDTO(
                t.getIdTeleconsulta(),
                t.getIdCita(),
                nombrePaciente,
                nombreDoctor,
                t.getEspecialidad(),
                t.getFecha().toString(),
                t.getHora().toString(),
                t.getEstado(),
                t.getUrlSesion(),
                t.getMotivo()
        );
    }
}

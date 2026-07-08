package edu.upn.clinica.backend.reserva.service;

import edu.upn.clinica.backend.asignacion.service.AsignacionAutomaticaService;
import edu.upn.clinica.backend.cita.dto.CitaPublicaResponse;
import edu.upn.clinica.backend.cita.model.Cita;
import edu.upn.clinica.backend.cita.repository.CitaRepository;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.doctor.dto.DoctorDisponibleDTO;
import edu.upn.clinica.backend.especialidad.repository.EspecialidadRepository;
import edu.upn.clinica.backend.especialidad.model.Especialidad;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.teleconsulta.notificacion.NotificacionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Service
public class ReservaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private AsignacionAutomaticaService asignacionService;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private SimpMessagingTemplate messaging;

    public CitaPublicaResponse reservarBasica(Integer idPaciente, LocalDate fecha,
                                               LocalTime hora, String motivo, String tipo) {
        Map<String, Integer> asignacion = asignacionService.encontrarDoctorDisponible(fecha, hora);
        Integer idDoctor = asignacion.get("idDoctor");
        Integer idConsultorio = asignacion.get("idConsultorio");

        Cita cita = new Cita();
        cita.setIdPaciente(idPaciente);
        cita.setIdDoctor(idDoctor);
        cita.setIdConsultorio(idConsultorio);
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setEstado("CONFIRMADA");
        cita.setTipo(tipo != null ? tipo : "PRESENCIAL");
        cita.setMotivo(motivo);
        cita.setTipoReserva("BASICA");

        cita = citaRepository.save(cita);
        return buildResponse(cita, null);
    }

    public CitaPublicaResponse reservarEspecialista(Integer idPaciente, Integer idEspecialidad,
                                                     LocalDate fecha, LocalTime hora,
                                                     String motivo, String tipo) {
        Especialidad esp = especialidadRepository.findById(idEspecialidad)
                .orElseThrow(() -> new AppException("Especialidad no encontrada", HttpStatus.NOT_FOUND));

        if (!"ACTIVO".equals(esp.getEstado())) {
            throw new AppException("Especialidad no disponible", HttpStatus.BAD_REQUEST);
        }

        Cita cita = new Cita();
        cita.setIdPaciente(idPaciente);
        cita.setIdDoctor(null);
        cita.setIdConsultorio(null);
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setEstado("PENDIENTE_ASIGNACION");
        cita.setTipo(tipo != null ? tipo : "PRESENCIAL");
        cita.setMotivo(motivo);
        cita.setTipoReserva("ESPECIALISTA");
        cita.setIdEspecialidad(idEspecialidad);
        cita.setMontoExtra(esp.getCostoExtra());

        cita = citaRepository.save(cita);
        messaging.convertAndSend("/topic/notificaciones/asistente",
                new NotificacionDTO("NUEVA_CITA_ESPECIALISTA",
                        "Nueva solicitud de cita con especialista: " + esp.getNombre(),
                        cita.getIdCita()));
        return buildResponse(cita, esp.getNombre());
    }

    private CitaPublicaResponse buildResponse(Cita cita, String especialidad) {
        CitaPublicaResponse resp = new CitaPublicaResponse();

        resp.setIdCita(cita.getIdCita());
        resp.setIdPaciente(cita.getIdPaciente());
        resp.setIdDoctor(cita.getIdDoctor());
        resp.setFecha(cita.getFecha().toString());
        resp.setHora(cita.getHora().toString());
        resp.setEstado(cita.getEstado());
        resp.setTipo(cita.getTipo());
        resp.setTipoReserva(cita.getTipoReserva());
        resp.setMontoExtra(cita.getMontoExtra());
        resp.setConsultorio(cita.getConsultorio());

        if (cita.getIdDoctor() != null) {
            DoctorDisponibleDTO doctor = doctorRepository.findById(cita.getIdDoctor()).orElse(null);
            if (doctor != null) {
                resp.setDoctor(doctor.getNombre());
                resp.setEspecialidad(doctor.getEspecialidad());
            }
        }

        if (especialidad != null) {
            resp.setEspecialidad(especialidad);
        }

        return resp;
    }
}

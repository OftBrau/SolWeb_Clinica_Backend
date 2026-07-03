package edu.upn.clinica.backend.consulta.service;

import edu.upn.clinica.backend.cita.model.Cita;
import edu.upn.clinica.backend.cita.repository.CitaRepository;
import edu.upn.clinica.backend.consulta.dto.ConsultaRequestDTO;
import edu.upn.clinica.backend.consulta.dto.ConsultaResponseDTO;
import edu.upn.clinica.backend.consulta.model.Consulta;
import edu.upn.clinica.backend.consulta.repository.ConsultaRepository;
import edu.upn.clinica.backend.doctor.dto.DoctorDisponibleDTO;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.paciente.model.Paciente;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.shared.EmailService;
import edu.upn.clinica.backend.teleconsulta.notificacion.NotificacionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultaService {

    @Autowired private ConsultaRepository  consultaRepository;
    @Autowired private CitaRepository      citaRepository;
    @Autowired private PacienteRepository  pacienteRepository;
    @Autowired private DoctorRepository    doctorRepository;
    @Autowired private SimpMessagingTemplate messaging;
    @Autowired private EmailService         emailService;

    public ConsultaResponseDTO iniciar(Integer idDoctor, ConsultaRequestDTO req) {
        Cita cita = citaRepository.findById(req.getIdCita())
                .orElseThrow(() -> new AppException("Cita no encontrada", HttpStatus.NOT_FOUND));

        if (!cita.getIdDoctor().equals(idDoctor)) {
            throw new AppException("Esta cita no te pertenece", HttpStatus.FORBIDDEN);
        }

        if (consultaRepository.findByCitaId(req.getIdCita()).isPresent()) {
            throw new AppException("Ya existe una consulta para esta cita", HttpStatus.CONFLICT);
        }

        Consulta c = new Consulta();
        c.setIdCita(req.getIdCita());
        c.setIdPaciente(cita.getIdPaciente());
        c.setIdDoctor(idDoctor);
        c.setDiagnosticoCie10(req.getDiagnosticoCie10());
        c.setDescripcionDiagnostico(req.getDescripcionDiagnostico());
        c.setTratamiento(req.getTratamiento());
        c.setPrescripcion(req.getPrescripcion());

        Consulta guardada = consultaRepository.save(c);

        pacienteRepository.findById(cita.getIdPaciente())
                .map(Paciente::getEmail)
                .ifPresent(email -> {
                    System.out.println(">>> [WS] Notificando cita atendida a paciente: " + email);
                    messaging.convertAndSend("/topic/notificaciones/paciente/" + email,
                            new NotificacionDTO("CITA_ATENDIDA",
                                    "Tu cita ha sido atendida por " + doctorRepository
                                            .findById(idDoctor)
                                            .map(d -> d.getNombre())
                                            .orElse("el doctor"),
                                    req.getIdCita()));
                });

        return toResponse(guardada);
    }

    public ConsultaResponseDTO obtenerPorId(Integer id, Integer idDoctor) {
        Consulta c = consultaRepository.findById(id)
                .orElseThrow(() -> new AppException("Consulta no encontrada", HttpStatus.NOT_FOUND));
        if (!c.getIdDoctor().equals(idDoctor)) {
            throw new AppException("No tienes acceso a esta consulta", HttpStatus.FORBIDDEN);
        }
        return toResponse(c);
    }

    public ConsultaResponseDTO registrarDiagnostico(Integer id, Integer idDoctor, String cie10, String descripcion) {
        Consulta c = validarAcceso(id, idDoctor);
        consultaRepository.updateDiagnostico(id, cie10, descripcion);
        c.setDiagnosticoCie10(cie10);
        c.setDescripcionDiagnostico(descripcion);
        return toResponse(c);
    }

    public ConsultaResponseDTO registrarTratamiento(Integer id, Integer idDoctor, String tratamiento) {
        Consulta c = validarAcceso(id, idDoctor);
        if (c.getDiagnosticoCie10() == null) {
            throw new AppException("Debe registrar el diagnóstico antes del tratamiento", HttpStatus.BAD_REQUEST);
        }
        consultaRepository.updateTratamiento(id, tratamiento);
        c.setTratamiento(tratamiento);
        return toResponse(c);
    }

    public ConsultaResponseDTO prescribir(Integer id, Integer idDoctor, String prescripcion) {
        Consulta c = validarAcceso(id, idDoctor);
        consultaRepository.updatePrescripcion(id, prescripcion);
        c.setPrescripcion(prescripcion);
        ConsultaResponseDTO dto = toResponse(c);

        pacienteRepository.findById(c.getIdPaciente())
                .ifPresent(pac -> {
                    String docNombre = doctorRepository.findById(idDoctor)
                            .map(d -> d.getNombre())
                            .orElse("Doctor");
                    System.out.println(">>> [EMAIL] Enviando historial a " + pac.getEmail());
                    emailService.enviarHistorialConsulta(
                            pac.getEmail(),
                            pac.getNombre() + " " + pac.getApellido(),
                            docNombre, "",
                            dto.getDiagnosticoCie10() + " - " + dto.getDescripcionDiagnostico(),
                            dto.getTratamiento(),
                            dto.getPrescripcion(),
                            c.getCreatedAt() != null ? c.getCreatedAt().toLocalDate().toString() : ""
                    );
                });

        return dto;
    }

    public List<ConsultaResponseDTO> listarPorPaciente(Integer idPaciente, Integer idDoctor) {
        return consultaRepository.findByPaciente(idPaciente)
                .stream()
                .filter(c -> c.getIdDoctor().equals(idDoctor))
                .map(this::toResponse)
                .toList();
    }

    private Consulta validarAcceso(Integer id, Integer idDoctor) {
        Consulta c = consultaRepository.findById(id)
                .orElseThrow(() -> new AppException("Consulta no encontrada", HttpStatus.NOT_FOUND));
        if (!c.getIdDoctor().equals(idDoctor)) {
            throw new AppException("No tienes acceso a esta consulta", HttpStatus.FORBIDDEN);
        }
        return c;
    }

    private ConsultaResponseDTO toResponse(Consulta c) {
        String nombrePaciente = pacienteRepository.findById(c.getIdPaciente())
                .map(p -> p.getNombre() + " " + p.getApellido())
                .orElse("Paciente #" + c.getIdPaciente());

        DoctorDisponibleDTO doc = doctorRepository.findById(c.getIdDoctor())
                .orElse(new DoctorDisponibleDTO(c.getIdDoctor(), "Desconocido", ""));

        return new ConsultaResponseDTO(c, nombrePaciente, doc.getNombre());
    }
}

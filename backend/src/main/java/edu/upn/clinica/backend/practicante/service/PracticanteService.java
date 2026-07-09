package edu.upn.clinica.backend.practicante.service;

import edu.upn.clinica.backend.consulta.repository.ConsultaRepository;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.hce.model.HistorialItem;
import edu.upn.clinica.backend.hce.repository.HceRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.practicante.dto.ActividadDTO;
import edu.upn.clinica.backend.practicante.dto.AsignarPracticanteRequest;
import edu.upn.clinica.backend.practicante.dto.ConsultaPracDTO;
import edu.upn.clinica.backend.practicante.dto.CrearActividadRequest;
import edu.upn.clinica.backend.practicante.dto.CrearEvaluacionRequest;
import edu.upn.clinica.backend.practicante.dto.EvaluacionDTO;
import edu.upn.clinica.backend.practicante.dto.PacienteAsignadoDTO;
import edu.upn.clinica.backend.practicante.dto.PracticanteDisponibleDTO;
import edu.upn.clinica.backend.practicante.model.ActividadPracticante;
import edu.upn.clinica.backend.practicante.model.EvaluacionPracticante;
import edu.upn.clinica.backend.practicante.model.PracticanteEntidad;
import edu.upn.clinica.backend.practicante.repository.PracticanteRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PracticanteService {

    @Autowired
    private PracticanteRepository practicanteRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private HceRepository hceRepository;

    public List<ActividadDTO> listarActividades(String email) {
        Integer idPracticante = practicanteRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException("Practicante no encontrado", HttpStatus.NOT_FOUND));

        return practicanteRepository.findActividadesByPracticante(idPracticante)
                .stream().map(this::toActividadDTO).toList();
    }

    public List<ActividadDTO> listarTodasLasActividades() {
        return practicanteRepository.findAllActividades()
                .stream().map(this::toActividadDTO).toList();
    }

    public ActividadDTO obtenerActividad(Integer id, String email) {
        Integer idPracticante = practicanteRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException("Practicante no encontrado", HttpStatus.NOT_FOUND));

        ActividadPracticante act = practicanteRepository.findActividadById(id)
                .orElseThrow(() -> new AppException("Actividad no encontrada", HttpStatus.NOT_FOUND));

        if (!act.getIdPracticante().equals(idPracticante)) {
            throw new AppException("Esta actividad no te pertenece", HttpStatus.FORBIDDEN);
        }
        return toActividadDTO(act);
    }

    public ConsultaPracDTO registrarConsulta(ConsultaPracDTO dto, String email) {
        Integer idPracticante = practicanteRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException("Practicante no encontrado", HttpStatus.NOT_FOUND));

        edu.upn.clinica.backend.consulta.model.Consulta c = new edu.upn.clinica.backend.consulta.model.Consulta();
        c.setIdCita(dto.getIdCita() != null ? dto.getIdCita() : 0);
        c.setIdPaciente(dto.getIdPaciente());
        c.setIdDoctor(idPracticante);
        c.setIdPracticante(idPracticante);
        c.setEstadoRevision("BORRADOR");
        c.setDiagnosticoCie10(dto.getDiagnostico());
        c.setDescripcionDiagnostico(dto.getMotivo());
        c.setTratamiento(dto.getReceta());
        c = consultaRepository.save(c);

        ConsultaPracDTO res = new ConsultaPracDTO();
        res.setIdConsulta(c.getIdConsulta());
        res.setIdCita(c.getIdCita());
        res.setIdPaciente(c.getIdPaciente());
        res.setPaciente(dto.getPaciente());
        res.setMotivo(c.getDescripcionDiagnostico());
        res.setDiagnostico(c.getDiagnosticoCie10());
        res.setReceta(c.getTratamiento());
        res.setEstado(c.getEstadoRevision());
        res.setFecha(c.getCreatedAt() != null ? c.getCreatedAt().toLocalDate().toString() : LocalDate.now().toString());
        return res;
    }

    public void enviarARevision(Integer idConsulta, String email) {
        Integer idPracticante = practicanteRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException("Practicante no encontrado", HttpStatus.NOT_FOUND));

        edu.upn.clinica.backend.consulta.model.Consulta c = consultaRepository.findById(idConsulta)
                .orElseThrow(() -> new AppException("Consulta no encontrada", HttpStatus.NOT_FOUND));

        if (!c.getIdDoctor().equals(idPracticante)) {
            throw new AppException("Esta consulta no te pertenece", HttpStatus.FORBIDDEN);
        }

        practicanteRepository.enviarARevision(idConsulta);
    }

    public List<EvaluacionDTO> listarEvaluaciones(String email) {
        Integer idPracticante = practicanteRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException("Practicante no encontrado", HttpStatus.NOT_FOUND));

        return practicanteRepository.findEvaluacionesByPracticante(idPracticante)
                .stream().map(this::toEvaluacionDTO).toList();
    }

    public List<PacienteAsignadoDTO> buscarPacientesAsignados(String email, String query) {
        Integer idPracticante = practicanteRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException("Practicante no encontrado", HttpStatus.NOT_FOUND));

        List<Object[]> rows = practicanteRepository.findPacientesAsignados(idPracticante, query);
        return rows.stream().map(row -> {
            PacienteAsignadoDTO dto = new PacienteAsignadoDTO();
            dto.setIdPaciente((Integer) row[0]);
            dto.setNombreCompleto((String) row[1]);
            dto.setCodigoEstudiante((String) row[2]);
            dto.setUltimaConsulta((String) row[3]);
            return dto;
        }).toList();
    }

    // ─── Admin: Asignación de practicantes ────────────

    public List<PracticanteEntidad> listarAsignaciones() {
        return practicanteRepository.findAllAsignaciones();
    }

    public List<PracticanteDisponibleDTO> listarPracticantesDisponibles() {
        return practicanteRepository.findPracticantesDisponibles()
                .stream().map(row -> {
                    PracticanteDisponibleDTO dto = new PracticanteDisponibleDTO();
                    dto.setIdPracticante((Integer) row[0]);
                    dto.setNombre((String) row[1]);
                    dto.setApellido((String) row[2]);
                    dto.setEmail((String) row[3]);
                    return dto;
                }).toList();
    }

    public void asignarPracticante(AsignarPracticanteRequest request) {
        practicanteRepository.asignar(request.getIdPracticante(), request.getIdSupervisor());
    }

    public void eliminarAsignacion(Integer idAsignacion) {
        practicanteRepository.eliminarAsignacion(idAsignacion);
    }

    // ─── Doctor: Gestión de practicantes asignados ────

    public List<PracticanteEntidad> listarMisPracticantes(Integer idDoctor) {
        return practicanteRepository.findAsignacionesByDoctor(idDoctor);
    }

    public List<ActividadDTO> listarActividadesComoSupervisor(Integer idSupervisor) {
        return practicanteRepository.findActividadesBySupervisor(idSupervisor)
                .stream().map(this::toActividadDTO).toList();
    }

    public ActividadDTO crearActividadParaPracticante(CrearActividadRequest request, Integer idSupervisor) {
        ActividadPracticante a = new ActividadPracticante();
        a.setIdPracticante(request.getIdPracticante());
        a.setTitulo(request.getTitulo());
        a.setDescripcion(request.getDescripcion());
        a.setTipo(request.getTipo());
        a.setFecha(request.getFecha() != null ? request.getFecha() : LocalDate.now());
        a.setHora(request.getHora());
        a.setEstado("PENDIENTE");
        a.setIdPaciente(request.getIdPaciente());
        a.setIdSupervisor(idSupervisor);
        a.setIdCita(request.getIdCita());
        a.setIdTeleconsulta(request.getIdTeleconsulta());
        a = practicanteRepository.crearActividad(a);
        Integer idPracTarea = practicanteRepository.findIdPracticanteByDoctorId(request.getIdPracticante());
        if (idPracTarea == null) idPracTarea = request.getIdPracticante();
        practicanteRepository.saveTarea(idSupervisor, idPracTarea, request.getTitulo(),
                request.getDescripcion(), request.getTipo(), "MEDIA", request.getFecha() != null ? request.getFecha().toString() : null);
        return toActividadDTO(a);
    }

    public List<EvaluacionDTO> listarEvaluacionesComoSupervisor(Integer idSupervisor) {
        return practicanteRepository.findEvaluacionesBySupervisor(idSupervisor)
                .stream().map(this::toEvaluacionDTO).toList();
    }

    public EvaluacionDTO evaluarPracticante(CrearEvaluacionRequest request, Integer idSupervisor) {
        EvaluacionPracticante e = new EvaluacionPracticante();
        e.setIdPracticante(request.getIdPracticante());
        e.setIdSupervisor(idSupervisor);
        e.setFecha(LocalDate.now());
        e.setPuntuacion(request.getPuntuacion());
        e.setComentario(request.getComentario());
        e = practicanteRepository.crearEvaluacion(e);
        return toEvaluacionDTO(e);
    }

    public List<HistorialItem> verHistoriaClinica(Integer idPaciente, String email) {
        Integer idPracticante = practicanteRepository.findIdByEmail(email)
                .orElseThrow(() -> new AppException("Practicante no encontrado", HttpStatus.NOT_FOUND));

        pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new AppException("Paciente no encontrado", HttpStatus.NOT_FOUND));

        return hceRepository.findByPaciente(idPaciente);
    }

    private ActividadDTO toActividadDTO(ActividadPracticante a) {
        ActividadDTO dto = new ActividadDTO();
        dto.setIdActividad(a.getIdActividad());
        dto.setTitulo(a.getTitulo());
        dto.setDescripcion(a.getDescripcion());
        dto.setTipo(a.getTipo());
        dto.setFecha(a.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE));
        dto.setHora(a.getHora() != null ? a.getHora().toString() : null);
        dto.setEstado(a.getEstado());

        if (a.getIdPaciente() != null) {
            pacienteRepository.findById(a.getIdPaciente()).ifPresent(p ->
                dto.setPaciente(p.getNombre() + " " + p.getApellido()));
        }
        if (a.getIdSupervisor() != null) {
            doctorRepository.findById(a.getIdSupervisor()).ifPresent(d ->
                dto.setSupervisor(d.getNombre()));
        }
        return dto;
    }

    private EvaluacionDTO toEvaluacionDTO(EvaluacionPracticante e) {
        EvaluacionDTO dto = new EvaluacionDTO();
        dto.setIdEvaluacion(e.getIdEvaluacion());
        dto.setFecha(e.getFecha().format(DateTimeFormatter.ISO_LOCAL_DATE));
        dto.setPuntuacion(e.getPuntuacion());
        dto.setComentario(e.getComentario());

        doctorRepository.findById(e.getIdSupervisor()).ifPresent(d ->
            dto.setSupervisor(d.getNombre()));

        return dto;
    }
}

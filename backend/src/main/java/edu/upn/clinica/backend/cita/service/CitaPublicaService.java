package edu.upn.clinica.backend.cita.service;

import edu.upn.clinica.backend.cita.dto.CitaPublicaRequest;
import edu.upn.clinica.backend.cita.dto.CitaPublicaResponse;
import edu.upn.clinica.backend.cita.model.Cita;
import edu.upn.clinica.backend.cita.repository.CitaRepository;
import edu.upn.clinica.backend.doctor.dto.DoctorDisponibleDTO;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.paciente.model.Paciente;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.shared.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

// ============================================================
//  CitaPublicaService.java  (con emails)
// ============================================================
@Service
public class CitaPublicaService {

    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private CitaRepository citaRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    // ─── 1. Buscar paciente existente ───────────────────────
    public Paciente buscarPaciente(String email, String codigoEstudiante) {
        return pacienteRepository
                .findByEmailAndCodigo(email, codigoEstudiante)
                .orElseThrow(() -> new AppException(
                "No encontramos tus datos. Verifica el correo y código.",
                HttpStatus.NOT_FOUND));
    }

    // ─── 2. Listar doctores por especialidad ─────────────────
    public List<DoctorDisponibleDTO> listarDoctoresPorEspecialidad(String especialidad) {
        List<DoctorDisponibleDTO> doctores = doctorRepository.findByEspecialidad(especialidad);
        if (doctores.isEmpty()) {
            throw new AppException(
                    "No hay doctores disponibles para esa especialidad.", HttpStatus.NOT_FOUND);
        }
        return doctores;
    }

    // ─── 3. Agendar cita ─────────────────────────────────────
    public CitaPublicaResponse agendar(CitaPublicaRequest req) {

        boolean esPacienteNuevo = req.getIdPaciente() == null
                && !pacienteRepository.existsByEmail(req.getEmail());

        // Registra paciente si es nuevo y retorna [idPaciente, passwordEnClaro]
        String[] info = resolverPaciente(req, esPacienteNuevo);
        Integer idPaciente = Integer.parseInt(info[0]);
        String passwordEnviar = info[1]; // null si ya existía

        // Resolver doctor por nombre
        Integer idDoctor = doctorRepository
                .findIdByNombreCompleto(req.getMedico())
                .orElseThrow(() -> new AppException(
                "Doctor no encontrado: " + req.getMedico(), HttpStatus.NOT_FOUND));

        // Parsear fecha y hora
        LocalDate fecha;
        LocalTime hora;
        try {
            fecha = LocalDate.parse(req.getFecha());
            hora = LocalTime.parse(req.getHora());
        } catch (DateTimeParseException e) {
            throw new AppException("Formato de fecha u hora inválido.", HttpStatus.BAD_REQUEST);
        }

        // Validar conflicto
        if (citaRepository.existeConflicto(idDoctor, fecha, hora)) {
            throw new AppException(
                    "Ese horario ya está ocupado. Elige otra hora.", HttpStatus.CONFLICT);
        }

        // Guardar cita
        Cita cita = new Cita();
        cita.setIdPaciente(idPaciente);
        cita.setIdDoctor(idDoctor);
        cita.setIdConsultorio(null);
        cita.setFecha(fecha);
        cita.setHora(hora);
        cita.setEstado("CONFIRMADA");
        cita.setTipo(req.getTipo() != null ? req.getTipo() : "PRESENCIAL");
        cita.setMotivo(req.getMotivo() != null ? req.getMotivo() : "Consulta general");
        Cita guardada = citaRepository.save(cita);

        // Datos para respuesta y correo
        DoctorDisponibleDTO doctor = doctorRepository
                .findById(idDoctor)
                .orElse(new DoctorDisponibleDTO(idDoctor, req.getMedico(), req.getEspecialidad()));

        String nombrePaciente = req.getNombre() != null
                ? req.getNombre() + " " + req.getApellido()
                : "Paciente #" + idPaciente;

        // ── Enviar correos (async) ──
        // Correo 1: bienvenida + credenciales (solo pacientes nuevos)
        if (esPacienteNuevo && passwordEnviar != null) {
            emailService.enviarCredenciales(
                    req.getEmail(), req.getNombre(), passwordEnviar);
        }
        // Correo 2: confirmación de cita (siempre)
        emailService.enviarConfirmacionCita(
                req.getEmail(), nombrePaciente,
                doctor.getNombre(), doctor.getEspecialidad(),
                fecha.toString(), hora.toString()
        );

        return new CitaPublicaResponse(
                guardada.getIdCita(), nombrePaciente, doctor.getNombre(),
                doctor.getEspecialidad(), fecha.toString(), hora.toString(),
                guardada.getEstado(), guardada.getTipo()
        );
    }

    // ─── Helper privado ──────────────────────────────────────
    private String[] resolverPaciente(CitaPublicaRequest req, boolean esNuevo) {

        // Paciente existente (buscó con email+código)
        if (req.getIdPaciente() != null) {
            return new String[]{req.getIdPaciente().toString(), null};
        }

        // Email ya registrado (llegó por flujo "nuevo" pero ya tiene cuenta)
        if (pacienteRepository.existsByEmail(req.getEmail())) {
            Integer id = pacienteRepository
                    .findByEmail(req.getEmail())
                    .map(Paciente::getIdPaciente)
                    .orElseThrow(() -> new AppException(
                    "El correo ya está registrado. Usa 'Ya tengo cuenta'.",
                    HttpStatus.CONFLICT));
            return new String[]{id.toString(), null};
        }

        // Paciente completamente nuevo
        // Si el frontend no envió password, generamos uno
        String passwordFinal = (req.getPassword() != null && !req.getPassword().isBlank())
                ? req.getPassword()
                : UUID.randomUUID().toString().substring(0, 8);

        Paciente nuevo = new Paciente();
        nuevo.setNombre(req.getNombre());
        nuevo.setApellido(req.getApellido());
        nuevo.setEmail(req.getEmail());
        nuevo.setTelefono(req.getTelefono());
        nuevo.setPasswordHash(passwordEncoder.encode(passwordFinal));
        nuevo.setCodigoEstudiante(req.getEmail() != null
                ? req.getEmail().split("@")[0] // extrae "n00328120" del email
                : null);
        nuevo.setFechaNacimiento(
                req.getFechaNacimiento() != null
                ? LocalDate.parse(req.getFechaNacimiento())
                : LocalDate.of(2000, 1, 1)
        );
        nuevo.setGenero(req.getGenero() != null ? req.getGenero() : "OTRO");
        nuevo.setTipoSangre(null);
        nuevo.setAlergias(null);
        nuevo.setEstado("ACTIVO");

        Paciente guardado = pacienteRepository.save(nuevo);

        // Retorna id + password en claro para enviarlo por correo
        return new String[]{guardado.getIdPaciente().toString(), passwordFinal};
    }
}

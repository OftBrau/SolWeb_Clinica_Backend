package edu.upn.clinica.backend.cita.service;

import edu.upn.clinica.backend.cita.dto.CitaPublicaResponse;
import edu.upn.clinica.backend.cita.model.Cita;
import edu.upn.clinica.backend.cita.repository.CitaRepository;
import edu.upn.clinica.backend.doctor.dto.DoctorDisponibleDTO;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class CitaPacienteService {

    @Autowired private CitaRepository     citaRepository;
    @Autowired private DoctorRepository   doctorRepository;
    @Autowired private PacienteRepository pacienteRepository;

    // ─── Listar citas del paciente autenticado ───────────────
    public List<CitaPublicaResponse> listarMisCitas(Integer idPaciente) {
        return citaRepository.findByPaciente(idPaciente)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ─── Cancelar cita ───────────────────────────────────────
    public void cancelar(Integer idCita, Integer idPaciente) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new AppException("Cita no encontrada", HttpStatus.NOT_FOUND));

        if (!cita.getIdPaciente().equals(idPaciente))
            throw new AppException("No tienes permiso para cancelar esta cita", HttpStatus.FORBIDDEN);

        if ("CANCELADA".equals(cita.getEstado()))
            throw new AppException("La cita ya está cancelada", HttpStatus.CONFLICT);

        citaRepository.cancelar(idCita);
    }

    // ─── Reprogramar cita ─────────────────────────────────────
    public CitaPublicaResponse reprogramar(Integer idCita, Integer idPaciente,
                                           String fecha, String hora) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new AppException("Cita no encontrada", HttpStatus.NOT_FOUND));

        if (!cita.getIdPaciente().equals(idPaciente))
            throw new AppException("No tienes permiso para reprogramar esta cita", HttpStatus.FORBIDDEN);

        LocalDate nuevaFecha = LocalDate.parse(fecha);
        LocalTime nuevaHora  = LocalTime.parse(hora);

        if (citaRepository.existeConflicto(cita.getIdDoctor(), nuevaFecha, nuevaHora))
            throw new AppException("Ese horario ya está ocupado. Elige otra hora.", HttpStatus.CONFLICT);

        citaRepository.reprogramar(idCita, nuevaFecha, nuevaHora);

        cita.setFecha(nuevaFecha);
        cita.setHora(nuevaHora);
        cita.setEstado("CONFIRMADA");
        return toResponse(cita);
    }

    // ─── Helper ──────────────────────────────────────────────
    private CitaPublicaResponse toResponse(Cita c) {
        DoctorDisponibleDTO doctor = doctorRepository
                .findById(c.getIdDoctor())
                .orElse(new DoctorDisponibleDTO(c.getIdDoctor(), "Desconocido", ""));

        String nombrePaciente = pacienteRepository.findById(c.getIdPaciente())
                .map(p -> p.getNombre() + " " + p.getApellido())
                .orElse("Paciente #" + c.getIdPaciente());

        return new CitaPublicaResponse(
                c.getIdCita(), nombrePaciente, doctor.getNombre(),
                doctor.getEspecialidad(), c.getFecha().toString(),
                c.getHora().toString(), c.getEstado(), c.getTipo()
        );
    }
}
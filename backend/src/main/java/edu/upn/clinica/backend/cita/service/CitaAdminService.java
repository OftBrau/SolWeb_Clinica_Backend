package edu.upn.clinica.backend.cita.service;

import edu.upn.clinica.backend.cita.dto.CitaPublicaResponse;
import edu.upn.clinica.backend.cita.model.Cita;
import edu.upn.clinica.backend.cita.repository.CitaRepository;
import edu.upn.clinica.backend.doctor.dto.DoctorDisponibleDTO;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.shared.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class CitaAdminService {

    @Autowired private CitaRepository citaRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private PacienteRepository pacienteRepository;

    public PageResult<CitaPublicaResponse> listarPorEstado(String estado, int page, int size) {
        List<Cita> citas = citaRepository.findAllByEstado(estado, page, size);
        long total = citaRepository.countByEstado(estado);
        List<CitaPublicaResponse> dtos = citas.stream().map(this::toResponse).toList();
        return new PageResult<>(dtos, total, page, size);
    }

    public CitaPublicaResponse confirmar(Integer idCita) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new AppException("Cita no encontrada", HttpStatus.NOT_FOUND));

        if ("CONFIRMADA".equals(cita.getEstado())) {
            throw new AppException("La cita ya está confirmada", HttpStatus.CONFLICT);
        }
        if ("CANCELADA".equals(cita.getEstado()) || "ATENDIDA".equals(cita.getEstado())) {
            throw new AppException("No se puede confirmar una cita " + cita.getEstado(), HttpStatus.CONFLICT);
        }

        citaRepository.confirmar(idCita);
        cita.setEstado("CONFIRMADA");
        return toResponse(cita);
    }

    public void cancelar(Integer idCita) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new AppException("Cita no encontrada", HttpStatus.NOT_FOUND));

        if ("CANCELADA".equals(cita.getEstado())) {
            throw new AppException("La cita ya está cancelada", HttpStatus.CONFLICT);
        }
        if ("ATENDIDA".equals(cita.getEstado())) {
            throw new AppException("No se puede cancelar una cita ya atendida", HttpStatus.CONFLICT);
        }

        citaRepository.cancelar(idCita);
    }

    public CitaPublicaResponse reprogramar(Integer idCita, String fecha, String hora) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new AppException("Cita no encontrada", HttpStatus.NOT_FOUND));

        if ("ATENDIDA".equals(cita.getEstado()) || "CANCELADA".equals(cita.getEstado())) {
            throw new AppException("No se puede reprogramar una cita " + cita.getEstado(), HttpStatus.CONFLICT);
        }

        LocalDate nuevaFecha = LocalDate.parse(fecha);
        LocalTime nuevaHora = LocalTime.parse(hora);

        if (citaRepository.existeConflicto(cita.getIdDoctor(), nuevaFecha, nuevaHora)) {
            throw new AppException("Ese horario ya está ocupado. Elige otra hora.", HttpStatus.CONFLICT);
        }

        citaRepository.reprogramar(idCita, nuevaFecha, nuevaHora);
        cita.setFecha(nuevaFecha);
        cita.setHora(nuevaHora);
        cita.setEstado("CONFIRMADA");
        return toResponse(cita);
    }

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

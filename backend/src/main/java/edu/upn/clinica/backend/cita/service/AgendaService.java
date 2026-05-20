package edu.upn.clinica.backend.cita.service;

import edu.upn.clinica.backend.cita.dto.AgendaItemResponse;
import edu.upn.clinica.backend.cita.model.Cita;
import edu.upn.clinica.backend.cita.repository.CitaRepository;
import edu.upn.clinica.backend.doctor.dto.DoctorDisponibleDTO;
import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AgendaService {

    @Autowired private CitaRepository     citaRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private DoctorRepository   doctorRepository;

    public List<AgendaItemResponse> verAgenda(Integer idDoctor, LocalDate fecha) {
        return citaRepository.findByDoctorAndFecha(idDoctor, fecha)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AgendaItemResponse> verTodasLasAgendas(LocalDate fecha) {
        return citaRepository.findAllByFecha(fecha)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AgendaItemResponse toResponse(Cita c) {
        String nombrePaciente = pacienteRepository.findById(c.getIdPaciente())
                .map(p -> p.getNombre() + " " + p.getApellido())
                .orElse("Paciente #" + c.getIdPaciente());

        DoctorDisponibleDTO doc = doctorRepository.findById(c.getIdDoctor())
                .orElse(new DoctorDisponibleDTO(c.getIdDoctor(), "Desconocido", ""));

        AgendaItemResponse r = new AgendaItemResponse();
        r.setIdCita(c.getIdCita());
        r.setHora(c.getHora().toString());
        r.setPaciente(nombrePaciente);
        r.setTipo(c.getTipo());
        r.setEstado(c.getEstado());
        r.setMotivo(c.getMotivo());
        r.setIdPaciente(c.getIdPaciente());
        r.setDoctor(doc.getNombre());
        r.setIdDoctor(c.getIdDoctor());
        return r;
    }
}

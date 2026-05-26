package edu.upn.clinica.backend.teleconsulta.service;

import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.teleconsulta.dto.SolicitarTeleconsultaRequest;
import edu.upn.clinica.backend.teleconsulta.dto.TeleconsultaDTO;
import edu.upn.clinica.backend.teleconsulta.model.Teleconsulta;
import edu.upn.clinica.backend.teleconsulta.repository.TeleconsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        return toDTO(guardada);
    }

    public TeleconsultaDTO obtenerPorId(Integer id) {
        Teleconsulta t = teleconsultaRepository.findById(id)
                .orElseThrow(() -> new AppException("Teleconsulta no encontrada",
                        HttpStatus.NOT_FOUND));
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

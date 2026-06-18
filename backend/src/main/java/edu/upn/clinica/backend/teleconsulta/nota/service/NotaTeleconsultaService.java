package edu.upn.clinica.backend.teleconsulta.nota.service;

import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.teleconsulta.model.Teleconsulta;
import edu.upn.clinica.backend.teleconsulta.nota.dto.CrearNotaRequest;
import edu.upn.clinica.backend.teleconsulta.nota.dto.NotaTeleconsultaDTO;
import edu.upn.clinica.backend.teleconsulta.nota.model.NotaTeleconsulta;
import edu.upn.clinica.backend.teleconsulta.nota.repository.NotaTeleconsultaRepository;
import edu.upn.clinica.backend.teleconsulta.repository.TeleconsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotaTeleconsultaService {

    @Autowired private NotaTeleconsultaRepository notaRepository;
    @Autowired private TeleconsultaRepository teleconsultaRepository;
    @Autowired private DoctorRepository doctorRepository;

    public NotaTeleconsultaDTO crear(Integer idTeleconsulta, Integer idDoctor, CrearNotaRequest req) {
        Teleconsulta t = teleconsultaRepository.findById(idTeleconsulta)
                .orElseThrow(() -> new AppException("Teleconsulta no encontrada", HttpStatus.NOT_FOUND));
        if (!t.getIdDoctor().equals(idDoctor)) {
            throw new AppException("Esta teleconsulta no te pertenece", HttpStatus.FORBIDDEN);
        }
        NotaTeleconsulta nota = new NotaTeleconsulta();
        nota.setIdTeleconsulta(idTeleconsulta);
        nota.setIdDoctor(idDoctor);
        nota.setContenido(req.getContenido());
        nota.setTipo(req.getTipo());
        nota.setCreatedAt(LocalDateTime.now());
        nota = notaRepository.save(nota);
        return toDTO(nota);
    }

    public List<NotaTeleconsultaDTO> listar(Integer idTeleconsulta) {
        return notaRepository.findByTeleconsulta(idTeleconsulta)
                .stream().map(this::toDTO).toList();
    }

    private NotaTeleconsultaDTO toDTO(NotaTeleconsulta n) {
        String nombreDoctor = doctorRepository.findById(n.getIdDoctor())
                .map(d -> d.getNombre())
                .orElse("Doctor #" + n.getIdDoctor());
        return new NotaTeleconsultaDTO(
                n.getIdNota(), n.getIdTeleconsulta(), nombreDoctor,
                n.getContenido(), n.getTipo(),
                n.getCreatedAt() != null ? n.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : null
        );
    }
}

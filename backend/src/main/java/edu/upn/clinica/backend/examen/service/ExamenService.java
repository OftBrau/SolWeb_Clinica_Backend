package edu.upn.clinica.backend.examen.service;

import edu.upn.clinica.backend.doctor.repository.DoctorRepository;
import edu.upn.clinica.backend.examen.dto.ExamenRequestDTO;
import edu.upn.clinica.backend.examen.dto.ExamenResponseDTO;
import edu.upn.clinica.backend.examen.model.Examen;
import edu.upn.clinica.backend.examen.repository.ExamenRepository;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamenService {

    @Autowired
    private ExamenRepository examenRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public ExamenResponseDTO solicitar(ExamenRequestDTO dto, Integer idDoctor) {
        if (!dto.getTipo().equals("LABORATORIO") && !dto.getTipo().equals("IMAGEN")) {
            throw new AppException("Tipo de examen inválido. Use LABORATORIO o IMAGEN");
        }

        Examen e = new Examen();
        e.setIdConsulta(dto.getIdConsulta());
        e.setIdPaciente(dto.getIdPaciente());
        e.setIdDoctor(idDoctor);
        e.setTipo(dto.getTipo());
        e.setNombreExamen(dto.getNombreExamen());
        e.setDescripcion(dto.getDescripcion());
        e = examenRepository.save(e);
        return toResponse(e);
    }

    public List<ExamenResponseDTO> listarPorPaciente(Integer idPaciente) {
        return examenRepository.findByPaciente(idPaciente).stream()
                .map(this::toResponse).toList();
    }

    public List<ExamenResponseDTO> listarPorDoctor(Integer idDoctor) {
        return examenRepository.findByDoctor(idDoctor).stream()
                .map(this::toResponse).toList();
    }

    public List<ExamenResponseDTO> listarPorConsulta(Integer idConsulta) {
        return examenRepository.findByConsulta(idConsulta).stream()
                .map(this::toResponse).toList();
    }

    public ExamenResponseDTO registrarResultado(Integer id, String resultado) {
        examenRepository.findById(id)
                .orElseThrow(() -> new AppException("Examen no encontrado", HttpStatus.NOT_FOUND));
        examenRepository.actualizarResultado(id, resultado);
        return toResponse(examenRepository.findById(id).orElseThrow());
    }

    private ExamenResponseDTO toResponse(Examen e) {
        ExamenResponseDTO dto = new ExamenResponseDTO();
        dto.setIdExamen(e.getIdExamen());
        dto.setIdConsulta(e.getIdConsulta());
        dto.setIdPaciente(e.getIdPaciente());
        dto.setIdDoctor(e.getIdDoctor());
        dto.setTipo(e.getTipo());
        dto.setNombreExamen(e.getNombreExamen());
        dto.setDescripcion(e.getDescripcion());
        dto.setResultado(e.getResultado());
        dto.setEstado(e.getEstado());
        dto.setCreatedAt(e.getCreatedAt());

        pacienteRepository.findById(e.getIdPaciente()).ifPresent(p ->
                dto.setNombrePaciente(p.getNombre() + " " + p.getApellido()));
        doctorRepository.findById(e.getIdDoctor()).ifPresent(d ->
                dto.setNombreDoctor(d.getNombre()));

        return dto;
    }
}

package edu.upn.clinica.backend.paciente.service;

import edu.upn.clinica.backend.paciente.dto.PacienteDTO;
import edu.upn.clinica.backend.paciente.model.Paciente;
import edu.upn.clinica.backend.paciente.repository.PacienteRepository;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.shared.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

// ============================================================
//  PacienteService.java
//  Reglas de negocio del módulo paciente
// ============================================================
@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- Listar con paginación ---
    public PageResult<PacienteDTO> listar(int page, int size) {
        List<Paciente> lista  = pacienteRepository.findAll(page, size);
        long           total  = pacienteRepository.count();
        List<PacienteDTO> dtos = lista.stream().map(this::toDTO).toList();
        return new PageResult<>(dtos, total, page, size);
    }

    // --- Buscar por ID ---
    public PacienteDTO buscarPorId(Integer id) {
        return pacienteRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() ->
                    new AppException("Paciente no encontrado", HttpStatus.NOT_FOUND));
    }

    // --- Registrar nuevo paciente ---
    public PacienteDTO registrar(PacienteDTO dto) {
        // Validar email duplicado
        if (pacienteRepository.existsByEmail(dto.getEmail())) {
            throw new AppException("El email ya está registrado", HttpStatus.CONFLICT);
        }

        Paciente p = toEntity(dto);
        p.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        p.setEstado("ACTIVO");

        Paciente guardado = pacienteRepository.save(p);
        return toDTO(guardado);
    }

    // --- Actualizar paciente ---
    public PacienteDTO actualizar(Integer id, PacienteDTO dto) {
        Paciente existente = pacienteRepository.findById(id)
                .orElseThrow(() ->
                    new AppException("Paciente no encontrado", HttpStatus.NOT_FOUND));

        existente.setNombre(dto.getNombre());
        existente.setApellido(dto.getApellido());
        existente.setTelefono(dto.getTelefono());
        existente.setCodigoEstudiante(dto.getCodigoEstudiante());
        existente.setFechaNacimiento(dto.getFechaNacimiento());
        existente.setGenero(dto.getGenero());
        existente.setTipoSangre(dto.getTipoSangre());
        existente.setAlergias(dto.getAlergias());

        pacienteRepository.update(existente);
        return toDTO(existente);
    }

    // --- Desactivar paciente (soft delete) ---
    public void desactivar(Integer id) {
        Paciente existente = pacienteRepository.findById(id)
                .orElseThrow(() ->
                    new AppException("Paciente no encontrado", HttpStatus.NOT_FOUND));
        pacienteRepository.deactivate(existente.getIdUsuario());
    }

    // --- Convertir Entity → DTO (sin exponer passwordHash) ---
    private PacienteDTO toDTO(Paciente p) {
        PacienteDTO dto = new PacienteDTO();
        dto.setIdPaciente(p.getIdPaciente());
        dto.setNombre(p.getNombre());
        dto.setApellido(p.getApellido());
        dto.setEmail(p.getEmail());
        dto.setTelefono(p.getTelefono());
        dto.setEstado(p.getEstado());
        dto.setCodigoEstudiante(p.getCodigoEstudiante());
        dto.setFechaNacimiento(p.getFechaNacimiento());
        dto.setGenero(p.getGenero());
        dto.setTipoSangre(p.getTipoSangre());
        dto.setAlergias(p.getAlergias());
        // passwordHash nunca se incluye en el DTO
        return dto;
    }

    // --- Convertir DTO → Entity ---
    private Paciente toEntity(PacienteDTO dto) {
        Paciente p = new Paciente();
        p.setNombre(dto.getNombre());
        p.setApellido(dto.getApellido());
        p.setEmail(dto.getEmail());
        p.setTelefono(dto.getTelefono());
        p.setCodigoEstudiante(dto.getCodigoEstudiante());
        p.setFechaNacimiento(dto.getFechaNacimiento());
        p.setGenero(dto.getGenero());
        p.setTipoSangre(dto.getTipoSangre());
        p.setAlergias(dto.getAlergias());
        return p;
    }
}
package edu.upn.clinica.backend.especialidad.service;

import edu.upn.clinica.backend.especialidad.dto.EspecialidadDTO;
import edu.upn.clinica.backend.especialidad.model.Especialidad;
import edu.upn.clinica.backend.especialidad.repository.EspecialidadRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EspecialidadService {

    @Autowired
    private EspecialidadRepository especialidadRepository;

    public List<EspecialidadDTO> listar() {
        return especialidadRepository.findAll().stream().map(EspecialidadDTO::new).toList();
    }

    public List<EspecialidadDTO> listarActivas() {
        return especialidadRepository.findAllActivas().stream().map(EspecialidadDTO::new).toList();
    }

    public EspecialidadDTO obtener(Integer id) {
        Especialidad e = especialidadRepository.findById(id)
                .orElseThrow(() -> new AppException("Especialidad no encontrada", HttpStatus.NOT_FOUND));
        return new EspecialidadDTO(e);
    }

    public EspecialidadDTO crear(EspecialidadDTO dto) {
        if (especialidadRepository.existsByNombre(dto.getNombre())) {
            throw new AppException("Ya existe una especialidad con ese nombre", HttpStatus.CONFLICT);
        }

        Especialidad e = new Especialidad();
        e.setNombre(dto.getNombre());
        e.setDescripcion(dto.getDescripcion());
        e.setEstado("ACTIVO");
        e.setCostoExtra(dto.getCostoExtra());
        e = especialidadRepository.save(e);
        return new EspecialidadDTO(e);
    }

    public EspecialidadDTO actualizar(Integer id, EspecialidadDTO dto) {
        especialidadRepository.findById(id)
                .orElseThrow(() -> new AppException("Especialidad no encontrada", HttpStatus.NOT_FOUND));

        if (especialidadRepository.existsByNombre(dto.getNombre())) {
            Especialidad existente = especialidadRepository.findAll().stream()
                    .filter(e -> e.getNombre().equalsIgnoreCase(dto.getNombre()))
                    .findFirst().orElse(null);
            if (existente != null && !existente.getIdEspecialidad().equals(id)) {
                throw new AppException("Ya existe otra especialidad con ese nombre", HttpStatus.CONFLICT);
            }
        }

        especialidadRepository.update(id, dto.getNombre(), dto.getDescripcion(), dto.getCostoExtra());
        return obtener(id);
    }

    public void desactivar(Integer id) {
        especialidadRepository.findById(id)
                .orElseThrow(() -> new AppException("Especialidad no encontrada", HttpStatus.NOT_FOUND));
        especialidadRepository.updateEstado(id, "INACTIVO");
    }
}

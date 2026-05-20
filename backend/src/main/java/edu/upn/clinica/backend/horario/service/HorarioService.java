package edu.upn.clinica.backend.horario.service;

import edu.upn.clinica.backend.especialidad.repository.EspecialidadRepository;
import edu.upn.clinica.backend.horario.dto.HorarioDTO;
import edu.upn.clinica.backend.horario.model.HorarioAtencion;
import edu.upn.clinica.backend.horario.repository.HorarioRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HorarioService {

    @Autowired
    private HorarioRepository horarioRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    public List<HorarioDTO> listar() {
        return horarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<HorarioDTO> listarPorEspecialidad(Integer idEspecialidad) {
        especialidadRepository.findById(idEspecialidad)
                .orElseThrow(() -> new AppException("Especialidad no encontrada", HttpStatus.NOT_FOUND));
        return horarioRepository.findByEspecialidad(idEspecialidad).stream().map(this::toDTO).toList();
    }

    public HorarioDTO obtener(Integer id) {
        HorarioAtencion h = horarioRepository.findById(id)
                .orElseThrow(() -> new AppException("Horario no encontrado", HttpStatus.NOT_FOUND));
        return toDTO(h);
    }

    public HorarioDTO crear(HorarioDTO dto) {
        especialidadRepository.findById(dto.getIdEspecialidad())
                .orElseThrow(() -> new AppException("Especialidad no encontrada", HttpStatus.NOT_FOUND));

        validarHorario(dto);

        HorarioAtencion h = new HorarioAtencion();
        h.setIdEspecialidad(dto.getIdEspecialidad());
        h.setDiaSemana(dto.getDiaSemana().toUpperCase());
        h.setHoraInicio(java.time.LocalTime.parse(dto.getHoraInicio()));
        h.setHoraFin(java.time.LocalTime.parse(dto.getHoraFin()));
        h = horarioRepository.save(h);
        return toDTO(h);
    }

    public HorarioDTO actualizar(Integer id, HorarioDTO dto) {
        horarioRepository.findById(id)
                .orElseThrow(() -> new AppException("Horario no encontrado", HttpStatus.NOT_FOUND));

        especialidadRepository.findById(dto.getIdEspecialidad())
                .orElseThrow(() -> new AppException("Especialidad no encontrada", HttpStatus.NOT_FOUND));

        validarHorario(dto);

        horarioRepository.update(id, dto.getIdEspecialidad(), dto.getDiaSemana().toUpperCase(),
                dto.getHoraInicio(), dto.getHoraFin());
        return obtener(id);
    }

    public void eliminar(Integer id) {
        horarioRepository.findById(id)
                .orElseThrow(() -> new AppException("Horario no encontrado", HttpStatus.NOT_FOUND));
        horarioRepository.delete(id);
    }

    private void validarHorario(HorarioDTO dto) {
        if (dto.getHoraInicio() != null && dto.getHoraFin() != null) {
            java.time.LocalTime inicio = java.time.LocalTime.parse(dto.getHoraInicio());
            java.time.LocalTime fin = java.time.LocalTime.parse(dto.getHoraFin());
            if (!fin.isAfter(inicio)) {
                throw new AppException("La hora de fin debe ser posterior a la hora de inicio");
            }
        }

        String dia = dto.getDiaSemana().toUpperCase();
        if (!List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO")
                .contains(dia)) {
            throw new AppException("Día de semana inválido. Use LUNES, MARTES, etc.");
        }
    }

    private HorarioDTO toDTO(HorarioAtencion h) {
        HorarioDTO dto = new HorarioDTO(h);
        especialidadRepository.findById(h.getIdEspecialidad()).ifPresent(
                e -> dto.setEspecialidadNombre(e.getNombre()));
        return dto;
    }
}

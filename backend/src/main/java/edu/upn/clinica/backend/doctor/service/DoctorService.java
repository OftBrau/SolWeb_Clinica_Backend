package edu.upn.clinica.backend.doctor.service;

import edu.upn.clinica.backend.doctor.dto.DoctorDTO;
import edu.upn.clinica.backend.doctor.dto.DisponibilidadDTO;
import edu.upn.clinica.backend.doctor.repository.DisponibilidadRepository;
import edu.upn.clinica.backend.doctor.repository.DisponibilidadRepository.DisponibilidadRow;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    @Autowired
    private DisponibilidadRepository disponibilidadRepository;

    public List<DisponibilidadDTO> listarDisponibilidad(Integer idDoctor) {
        return disponibilidadRepository.findByDoctor(idDoctor).stream()
                .map(this::toDisponibilidadDTO)
                .toList();
    }

    public DisponibilidadDTO crearDisponibilidad(DisponibilidadDTO dto) {
        if (disponibilidadRepository.existeConflicto(
                dto.getIdDoctor(), dto.getDiaSemana(), dto.getHoraInicio(), dto.getHoraFin())) {
            throw new AppException("Ya existe disponibilidad en ese horario", HttpStatus.CONFLICT);
        }
        DisponibilidadRow row = new DisponibilidadRow();
        row.setIdDoctor(dto.getIdDoctor());
        row.setDiaSemana(dto.getDiaSemana());
        row.setHoraInicio(dto.getHoraInicio());
        row.setHoraFin(dto.getHoraFin());
        row = disponibilidadRepository.save(row);
        return toDisponibilidadDTO(row);
    }

    public DisponibilidadDTO actualizarDisponibilidad(Integer id, DisponibilidadDTO dto) {
        DisponibilidadRow row = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new AppException("Disponibilidad no encontrada", HttpStatus.NOT_FOUND));
        row.setDiaSemana(dto.getDiaSemana());
        row.setHoraInicio(dto.getHoraInicio());
        row.setHoraFin(dto.getHoraFin());
        disponibilidadRepository.update(row);
        return toDisponibilidadDTO(row);
    }

    public void eliminarDisponibilidad(Integer id) {
        disponibilidadRepository.findById(id)
                .orElseThrow(() -> new AppException("Disponibilidad no encontrada", HttpStatus.NOT_FOUND));
        disponibilidadRepository.delete(id);
    }

    private DisponibilidadDTO toDisponibilidadDTO(DisponibilidadRow row) {
        return new DisponibilidadDTO(
                row.getIdDisponibilidad(), row.getIdDoctor(),
                row.getDiaSemana(), row.getHoraInicio(), row.getHoraFin());
    }
}

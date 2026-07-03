package edu.upn.clinica.backend.consultorio.service;

import edu.upn.clinica.backend.consultorio.dto.AsignarConsultorioRequest;
import edu.upn.clinica.backend.consultorio.dto.ConsultorioDTO;
import edu.upn.clinica.backend.consultorio.model.Consultorio;
import edu.upn.clinica.backend.consultorio.repository.ConsultorioRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConsultorioService {

    @Autowired
    private ConsultorioRepository consultorioRepository;

    public List<ConsultorioDTO> listar() {
        return consultorioRepository.findAll().stream().map(ConsultorioDTO::new).toList();
    }

    public ConsultorioDTO obtener(Integer id) {
        Consultorio c = consultorioRepository.findById(id)
                .orElseThrow(() -> new AppException("Consultorio no encontrado", HttpStatus.NOT_FOUND));
        return new ConsultorioDTO(c);
    }

    public ConsultorioDTO crear(ConsultorioDTO dto) {
        Consultorio c = new Consultorio();
        c.setNombre(dto.getNombre());
        c.setUbicacion(dto.getUbicacion());
        c.setEstado("ACTIVO");
        c = consultorioRepository.save(c);
        return new ConsultorioDTO(c);
    }

    public ConsultorioDTO actualizar(Integer id, ConsultorioDTO dto) {
        consultorioRepository.findById(id)
                .orElseThrow(() -> new AppException("Consultorio no encontrado", HttpStatus.NOT_FOUND));
        consultorioRepository.update(id, dto.getNombre(), dto.getUbicacion());
        return obtener(id);
    }

    public void desactivar(Integer id) {
        consultorioRepository.findById(id)
                .orElseThrow(() -> new AppException("Consultorio no encontrado", HttpStatus.NOT_FOUND));
        consultorioRepository.updateEstado(id, "INACTIVO");
    }

    public void activar(Integer id) {
        consultorioRepository.findById(id)
                .orElseThrow(() -> new AppException("Consultorio no encontrado", HttpStatus.NOT_FOUND));
        consultorioRepository.updateEstado(id, "ACTIVO");
    }

    public List<Map<String, Object>> listarAsignaciones() {
        return consultorioRepository.findAllAsignaciones();
    }

    public void eliminarAsignacion(Integer id) {
        consultorioRepository.deleteAsignacion(id);
    }

    public List<Map<String, Object>> ocupacion(String fecha) {
        return consultorioRepository.findOcupacion(fecha);
    }

    public void asignarADoctor(AsignarConsultorioRequest request) {
        consultorioRepository.findById(request.getIdConsultorio())
                .orElseThrow(() -> new AppException("Consultorio no encontrado", HttpStatus.NOT_FOUND));

        if (consultorioRepository.existeConflicto(
                request.getIdConsultorio(), request.getIdDoctor(),
                request.getDiaSemana(), request.getHoraInicio(), request.getHoraFin())) {
            throw new AppException("El consultorio ya está asignado a otro doctor en ese horario",
                    HttpStatus.CONFLICT);
        }

        consultorioRepository.asignarADoctor(
                request.getIdConsultorio(), request.getIdDoctor(),
                request.getDiaSemana(), request.getHoraInicio(), request.getHoraFin());
    }

    public void reasignar(Integer idAsignacion, Integer nuevoIdConsultorio) {
        throw new AppException("No implementado", HttpStatus.NOT_IMPLEMENTED);
    }

    public List<ConsultorioDTO> disponibles(String diaSemana, String hora) {
        return consultorioRepository.findDisponiblesPorHorario(diaSemana, hora)
                .stream().map(ConsultorioDTO::new).toList();
    }
}

package edu.upn.clinica.backend.enfermero.service;

import edu.upn.clinica.backend.enfermero.dto.TriajeRequest;
import edu.upn.clinica.backend.enfermero.dto.TriajeResponse;
import edu.upn.clinica.backend.enfermero.model.AsignacionEnfermero;
import edu.upn.clinica.backend.enfermero.model.Triaje;
import edu.upn.clinica.backend.enfermero.repository.EnfermeroRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EnfermeroService {

    @Autowired
    private EnfermeroRepository enfermeroRepository;

    public List<AsignacionEnfermero> listarEnfermerosPorDoctor(Integer idDoctor) {
        return enfermeroRepository.findByDoctor(idDoctor);
    }

    public List<AsignacionEnfermero> listarEnfermerosDisponibles() {
        return enfermeroRepository.findEnfermerosDisponibles();
    }

    public void asignarEnfermero(Integer idEnfermero, Integer idDoctor) {
        enfermeroRepository.findDoctorByEnfermero(idEnfermero)
                .ifPresent(d -> {
                    throw new AppException("El enfermero ya esta asignado a un doctor", HttpStatus.CONFLICT);
                });
        enfermeroRepository.asignar(idEnfermero, idDoctor);
    }

    public void desasignarEnfermero(Integer idAsignacion) {
        enfermeroRepository.desasignar(idAsignacion);
    }

    public TriajeResponse registrarTriaje(TriajeRequest request, Integer idEnfermero) {
        Optional<Triaje> existente = enfermeroRepository.findTriajeByCita(request.getIdCita());

        Triaje triaje = new Triaje();
        triaje.setIdCita(request.getIdCita());
        triaje.setIdEnfermero(idEnfermero);
        triaje.setPresionArterial(request.getPresionArterial());
        triaje.setTemperatura(request.getTemperatura());
        triaje.setFrecuenciaCardiaca(request.getFrecuenciaCardiaca());
        triaje.setSaturacion(request.getSaturacion());
        triaje.setPeso(request.getPeso());
        triaje.setTalla(request.getTalla());
        triaje.setMotivoConsulta(request.getMotivoConsulta());
        triaje.setNotas(request.getNotas());

        if (existente.isPresent()) {
            enfermeroRepository.updateTriaje(triaje);
            triaje.setIdTriaje(existente.get().getIdTriaje());
        } else {
            triaje = enfermeroRepository.saveTriaje(triaje);
        }

        return buildResponse(triaje);
    }

    public TriajeResponse obtenerTriaje(Integer idCita) {
        Triaje triaje = enfermeroRepository.findTriajeByCita(idCita)
                .orElseThrow(() -> new AppException("Triaje no encontrado para esta cita", HttpStatus.NOT_FOUND));
        return buildResponse(triaje);
    }

    public Optional<Integer> obtenerDoctorDeEnfermero(Integer idEnfermero) {
        return enfermeroRepository.findDoctorByEnfermero(idEnfermero);
    }

    private TriajeResponse buildResponse(Triaje t) {
        TriajeResponse resp = new TriajeResponse();
        resp.setIdTriaje(t.getIdTriaje());
        resp.setIdCita(t.getIdCita());
        resp.setIdEnfermero(t.getIdEnfermero());
        resp.setPresionArterial(t.getPresionArterial());
        resp.setTemperatura(t.getTemperatura());
        resp.setFrecuenciaCardiaca(t.getFrecuenciaCardiaca());
        resp.setSaturacion(t.getSaturacion());
        resp.setPeso(t.getPeso());
        resp.setTalla(t.getTalla());
        resp.setMotivoConsulta(t.getMotivoConsulta());
        resp.setNotas(t.getNotas());
        resp.setCreatedAt(t.getCreatedAt());
        return resp;
    }
}

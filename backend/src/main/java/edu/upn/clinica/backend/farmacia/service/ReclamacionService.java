package edu.upn.clinica.backend.farmacia.service;

import edu.upn.clinica.backend.farmacia.dto.CrearReclamacionRequest;
import edu.upn.clinica.backend.farmacia.dto.ReclamacionDTO;
import edu.upn.clinica.backend.farmacia.model.Reclamacion;
import edu.upn.clinica.backend.farmacia.repository.ReclamacionRepository;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.shared.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReclamacionService {

    @Autowired
    private ReclamacionRepository reclamacionRepository;

    public PageResult<ReclamacionDTO> listar(int page, int size) {
        List<Reclamacion> lista = reclamacionRepository.findAll(page, size);
        long total = reclamacionRepository.count();
        List<ReclamacionDTO> dtos = lista.stream().map(this::toDTO).toList();
        return new PageResult<>(dtos, total, page, size);
    }

    public ReclamacionDTO buscarPorId(Integer id) {
        return reclamacionRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new AppException("Reclamacion no encontrada", HttpStatus.NOT_FOUND));
    }

    public List<ReclamacionDTO> listarPorPaciente(Integer idPaciente) {
        return reclamacionRepository.findByPaciente(idPaciente).stream()
                .map(this::toDTO).toList();
    }

    public ReclamacionDTO crear(Integer idPaciente, CrearReclamacionRequest request) {
        Reclamacion r = new Reclamacion();
        r.setIdPaciente(idPaciente);
        r.setNombreCompleto(request.getNombreCompleto());
        r.setApellidos(request.getApellidos());
        r.setDni(request.getDni());
        r.setDireccion(request.getDireccion());
        r.setEmail(request.getEmail());
        r.setTelefono(request.getTelefono());
        r.setTipo(request.getTipo().toUpperCase());
        r.setDescripcion(request.getDescripcion());
        r.setProductoServicio(request.getProductoServicio());
        r.setEstado("PENDIENTE");
        return toDTO(reclamacionRepository.save(r));
    }

    public ReclamacionDTO crearAnonimo(CrearReclamacionRequest request) {
        Reclamacion r = new Reclamacion();
        r.setNombreCompleto(request.getNombreCompleto());
        r.setApellidos(request.getApellidos());
        r.setDni(request.getDni());
        r.setDireccion(request.getDireccion());
        r.setEmail(request.getEmail());
        r.setTelefono(request.getTelefono());
        r.setTipo(request.getTipo().toUpperCase());
        r.setDescripcion(request.getDescripcion());
        r.setProductoServicio(request.getProductoServicio());
        r.setEstado("PENDIENTE");
        return toDTO(reclamacionRepository.save(r));
    }

    public ReclamacionDTO responder(Integer id, String estado, String respuesta) {
        Reclamacion r = reclamacionRepository.findById(id)
                .orElseThrow(() -> new AppException("Reclamacion no encontrada", HttpStatus.NOT_FOUND));
        reclamacionRepository.updateEstado(id, estado, respuesta);
        r.setEstado(estado);
        r.setRespuesta(respuesta);
        return toDTO(r);
    }

    private ReclamacionDTO toDTO(Reclamacion r) {
        ReclamacionDTO d = new ReclamacionDTO();
        d.setIdReclamacion(r.getIdReclamacion());
        d.setIdPaciente(r.getIdPaciente());
        d.setNombreCompleto(r.getNombreCompleto());
        d.setApellidos(r.getApellidos());
        d.setDni(r.getDni());
        d.setDireccion(r.getDireccion());
        d.setEmail(r.getEmail());
        d.setTelefono(r.getTelefono());
        d.setTipo(r.getTipo());
        d.setDescripcion(r.getDescripcion());
        d.setProductoServicio(r.getProductoServicio());
        d.setEstado(r.getEstado());
        d.setRespuesta(r.getRespuesta());
        d.setFechaCreacion(r.getFechaCreacion());
        d.setFechaRespuesta(r.getFechaRespuesta());
        return d;
    }
}

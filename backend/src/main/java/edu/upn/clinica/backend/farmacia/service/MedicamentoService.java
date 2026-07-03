package edu.upn.clinica.backend.farmacia.service;

import edu.upn.clinica.backend.farmacia.dto.MedicamentoDTO;
import edu.upn.clinica.backend.farmacia.model.Medicamento;
import edu.upn.clinica.backend.farmacia.repository.MedicamentoRepository;
import edu.upn.clinica.backend.shared.AppException;
import edu.upn.clinica.backend.shared.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    public PageResult<MedicamentoDTO> listar(int page, int size) {
        List<Medicamento> lista = medicamentoRepository.findAll(page, size);
        long total = medicamentoRepository.count();
        List<MedicamentoDTO> dtos = lista.stream().map(this::toDTO).toList();
        return new PageResult<>(dtos, total, page, size);
    }

    public List<MedicamentoDTO> listarActivos() {
        return medicamentoRepository.findAllActivos().stream().map(this::toDTO).toList();
    }

    public MedicamentoDTO buscarPorId(Integer id) {
        return medicamentoRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new AppException("Medicamento no encontrado", HttpStatus.NOT_FOUND));
    }

    public MedicamentoDTO crear(MedicamentoDTO dto) {
        Medicamento m = toEntity(dto);
        m.setActivo(true);
        Medicamento guardado = medicamentoRepository.save(m);
        return toDTO(guardado);
    }

    public MedicamentoDTO actualizar(Integer id, MedicamentoDTO dto) {
        Medicamento existente = medicamentoRepository.findById(id)
                .orElseThrow(() -> new AppException("Medicamento no encontrado", HttpStatus.NOT_FOUND));
        existente.setNombreComercial(dto.getNombreComercial());
        existente.setNombreGenerico(dto.getNombreGenerico());
        existente.setPresentacion(dto.getPresentacion());
        existente.setConcentracion(dto.getConcentracion());
        existente.setLaboratorio(dto.getLaboratorio());
        existente.setStock(dto.getStock());
        existente.setPrecioUnitario(dto.getPrecioUnitario());
        existente.setRequiereReceta(dto.getRequiereReceta());
        existente.setDescripcion(dto.getDescripcion());
        existente.setFechaVencimiento(dto.getFechaVencimiento());
        medicamentoRepository.update(existente);
        return toDTO(existente);
    }

    public void desactivar(Integer id) {
        medicamentoRepository.findById(id)
                .orElseThrow(() -> new AppException("Medicamento no encontrado", HttpStatus.NOT_FOUND));
        medicamentoRepository.deactivate(id);
    }

    private MedicamentoDTO toDTO(Medicamento m) {
        MedicamentoDTO d = new MedicamentoDTO();
        d.setIdMedicamento(m.getIdMedicamento());
        d.setNombreComercial(m.getNombreComercial());
        d.setNombreGenerico(m.getNombreGenerico());
        d.setPresentacion(m.getPresentacion());
        d.setConcentracion(m.getConcentracion());
        d.setLaboratorio(m.getLaboratorio());
        d.setStock(m.getStock());
        d.setPrecioUnitario(m.getPrecioUnitario());
        d.setRequiereReceta(m.getRequiereReceta());
        d.setDescripcion(m.getDescripcion());
        d.setFotoUrl(m.getFotoUrl());
        d.setFechaVencimiento(m.getFechaVencimiento());
        d.setActivo(m.getActivo());
        d.setCategoria(m.getCategoria());
        return d;
    }

    private Medicamento toEntity(MedicamentoDTO d) {
        Medicamento m = new Medicamento();
        m.setIdMedicamento(d.getIdMedicamento());
        m.setNombreComercial(d.getNombreComercial());
        m.setNombreGenerico(d.getNombreGenerico());
        m.setPresentacion(d.getPresentacion());
        m.setConcentracion(d.getConcentracion());
        m.setLaboratorio(d.getLaboratorio());
        m.setStock(d.getStock());
        m.setPrecioUnitario(d.getPrecioUnitario());
        m.setRequiereReceta(d.getRequiereReceta());
        m.setDescripcion(d.getDescripcion());
        m.setFotoUrl(d.getFotoUrl());
        m.setFechaVencimiento(d.getFechaVencimiento());
        m.setActivo(d.getActivo());
        m.setCategoria(d.getCategoria());
        return m;
    }
}

package edu.upn.clinica.backend.farmacia.service;

import edu.upn.clinica.backend.farmacia.dto.CarritoItemDTO;
import edu.upn.clinica.backend.farmacia.model.CarritoItem;
import edu.upn.clinica.backend.farmacia.model.Medicamento;
import edu.upn.clinica.backend.farmacia.repository.CarritoRepository;
import edu.upn.clinica.backend.farmacia.repository.MedicamentoRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired
    private MedicamentoRepository medicamentoRepository;

    public List<CarritoItemDTO> listar(Integer idPaciente) {
        return carritoRepository.findByPaciente(idPaciente).stream()
                .map(this::toDTO)
                .toList();
    }

    public int contar(Integer idPaciente) {
        return carritoRepository.countByPaciente(idPaciente);
    }

    public CarritoItemDTO agregar(Integer idPaciente, Integer idMedicamento, int cantidad) {
        Medicamento med = medicamentoRepository.findById(idMedicamento)
                .orElseThrow(() -> new AppException("Producto no encontrado", HttpStatus.NOT_FOUND));

        if (!med.getActivo() || med.getStock() < cantidad) {
            throw new AppException("Stock insuficiente", HttpStatus.BAD_REQUEST);
        }

        CarritoItem item = carritoRepository.addOrUpdate(idPaciente, idMedicamento, cantidad);
        if (item.getNombreComercial() == null) {
            item.setNombreComercial(med.getNombreComercial());
            item.setPrecioUnitario(med.getPrecioUnitario());
            item.setCategoria(med.getCategoria());
        }
        return toDTO(item);
    }

    public CarritoItemDTO actualizarCantidad(Integer idPaciente, Integer idCarrito, int cantidad) {
        if (cantidad <= 0) {
            carritoRepository.remove(idCarrito);
            return null;
        }
        carritoRepository.updateCantidad(idCarrito, cantidad);
        List<CarritoItem> items = carritoRepository.findByPaciente(idPaciente);
        return items.stream()
                .filter(i -> i.getIdCarrito().equals(idCarrito))
                .findFirst()
                .map(this::toDTO)
                .orElseThrow(() -> new AppException("Item no encontrado", HttpStatus.NOT_FOUND));
    }

    public void eliminar(Integer idCarrito) {
        carritoRepository.remove(idCarrito);
    }

    public void vaciar(Integer idPaciente) {
        carritoRepository.clearByPaciente(idPaciente);
    }

    private CarritoItemDTO toDTO(CarritoItem item) {
        CarritoItemDTO d = new CarritoItemDTO();
        d.setIdCarrito(item.getIdCarrito());
        d.setIdMedicamento(item.getIdMedicamento());
        d.setNombreComercial(item.getNombreComercial());
        d.setPrecioUnitario(item.getPrecioUnitario());
        d.setCategoria(item.getCategoria());
        d.setCantidad(item.getCantidad());
        d.setSubtotal(item.getPrecioUnitario() != null
                ? item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()))
                : BigDecimal.ZERO);
        return d;
    }
}

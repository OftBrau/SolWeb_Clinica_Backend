package edu.upn.clinica.backend.farmacia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.upn.clinica.backend.farmacia.dto.*;
import edu.upn.clinica.backend.farmacia.model.DetalleVenta;
import edu.upn.clinica.backend.farmacia.model.Medicamento;
import edu.upn.clinica.backend.farmacia.model.Venta;
import edu.upn.clinica.backend.farmacia.repository.MedicamentoRepository;
import edu.upn.clinica.backend.farmacia.repository.CarritoRepository;
import edu.upn.clinica.backend.farmacia.repository.VentaRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private MedicamentoRepository medicamentoRepository;
    @Autowired
    private CarritoRepository carritoRepository;
    @Autowired(required = false)
    private MercadoPagoService mercadoPagoService;

    @Transactional
    public VentaResponseDTO crearVenta(Integer idPaciente, CrearVentaRequest request) {
        BigDecimal total = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();

        for (CrearVentaRequest.ItemVenta item : request.getItems()) {
            Medicamento med = medicamentoRepository.findById(item.getIdMedicamento())
                    .orElseThrow(() -> new AppException(
                            "Medicamento no encontrado: " + item.getIdMedicamento(), HttpStatus.NOT_FOUND));

            if (!med.getActivo() || med.getStock() < item.getCantidad()) {
                throw new AppException("Stock insuficiente para: " + med.getNombreComercial(),
                        HttpStatus.BAD_REQUEST);
            }

            BigDecimal subtotal = med.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);

            DetalleVenta d = new DetalleVenta();
            d.setIdMedicamento(med.getIdMedicamento());
            d.setCantidad(item.getCantidad());
            d.setPrecioUnitario(med.getPrecioUnitario());
            d.setSubtotal(subtotal);
            d.setNombreComercial(med.getNombreComercial());
            detalles.add(d);
        }

        Venta venta = new Venta();
        venta.setIdPaciente(idPaciente);
        venta.setTotal(total);
        venta.setEstado("PENDIENTE");
        venta.setMetodoPago("MERCADOPAGO");

        venta = ventaRepository.save(venta);

        for (DetalleVenta d : detalles) {
            d.setIdVenta(venta.getIdVenta());
            ventaRepository.saveDetalle(d);
            medicamentoRepository.updateStock(d.getIdMedicamento(), -d.getCantidad());
        }

        venta.setDetalles(detalles);
        return toDTO(venta);
    }

    @Transactional
    public VentaResponseDTO crearVentaConMercadoPago(Integer idPaciente, CrearVentaRequest request) {
        VentaResponseDTO ventaDTO = crearVenta(idPaciente, request);
        carritoRepository.clearByPaciente(idPaciente);
        if (mercadoPagoService != null) {
            String initPoint = mercadoPagoService.crearPreferencia(ventaDTO.getIdVenta(), ventaDTO.getDetalles());
            ventaDTO.setInitPoint(initPoint);
        }
        return ventaDTO;
    }

    public VentaResponseDTO buscarPorId(Integer id) {
        return ventaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new AppException("Venta no encontrada", HttpStatus.NOT_FOUND));
    }

    public List<VentaResponseDTO> listarPorPaciente(Integer idPaciente) {
        return ventaRepository.findByPaciente(idPaciente).stream()
                .map(v -> {
                    v.setDetalles(ventaRepository.findDetallesByVenta(v.getIdVenta()));
                    return toDTO(v);
                })
                .toList();
    }

    @Transactional
    public VentaResponseDTO checkoutPublic(CheckoutPublicRequest request) {
        BigDecimal total = BigDecimal.ZERO;
        List<DetalleVenta> detalles = new ArrayList<>();

        for (CheckoutPublicRequest.ItemCheckout item : request.getItems()) {
            Medicamento med = medicamentoRepository.findById(item.getIdMedicamento())
                    .orElseThrow(() -> new AppException("Medicamento no encontrado", HttpStatus.NOT_FOUND));
            if (!med.getActivo() || med.getStock() < item.getCantidad())
                throw new AppException("Stock insuficiente para: " + med.getNombreComercial(), HttpStatus.BAD_REQUEST);

            BigDecimal subtotal = med.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);
            DetalleVenta d = new DetalleVenta();
            d.setIdMedicamento(med.getIdMedicamento());
            d.setCantidad(item.getCantidad());
            d.setPrecioUnitario(med.getPrecioUnitario());
            d.setSubtotal(subtotal);
            d.setNombreComercial(med.getNombreComercial());
            detalles.add(d);
        }

        Venta venta = ventaRepository.savePublic(request.getNombre(), request.getEmail(), request.getTelefono(), total, "PENDIENTE", "MERCADOPAGO");

        for (DetalleVenta d : detalles) {
            d.setIdVenta(venta.getIdVenta());
            ventaRepository.saveDetalle(d);
            medicamentoRepository.updateStock(d.getIdMedicamento(), -d.getCantidad());
        }

        VentaResponseDTO dto = toDTO(venta);
        dto.setDetalles(detalles.stream().map(d -> {
            DetalleVentaDTO dd = new DetalleVentaDTO();
            dd.setIdDetalle(d.getIdDetalle());
            dd.setIdMedicamento(d.getIdMedicamento());
            dd.setNombreComercial(d.getNombreComercial());
            dd.setCantidad(d.getCantidad());
            dd.setPrecioUnitario(d.getPrecioUnitario());
            dd.setSubtotal(d.getSubtotal());
            return dd;
        }).toList());

        if (mercadoPagoService != null) {
            String initPoint = mercadoPagoService.crearPreferencia(dto.getIdVenta(), dto.getDetalles());
            dto.setInitPoint(initPoint);
        }
        return dto;
    }

    private VentaResponseDTO toDTO(Venta v) {
        VentaResponseDTO d = new VentaResponseDTO();
        d.setIdVenta(v.getIdVenta());
        d.setIdPaciente(v.getIdPaciente());
        d.setFechaVenta(v.getFechaVenta());
        d.setTotal(v.getTotal());
        d.setEstado(v.getEstado());
        d.setMetodoPago(v.getMetodoPago());
        d.setIdPreferenciaMp(v.getIdPreferenciaMp());
        d.setIdPagoMp(v.getIdPagoMp());
        if (v.getDetalles() != null) {
            d.setDetalles(v.getDetalles().stream().map(det -> {
                DetalleVentaDTO dd = new DetalleVentaDTO();
                dd.setIdDetalle(det.getIdDetalle());
                dd.setIdMedicamento(det.getIdMedicamento());
                dd.setNombreComercial(det.getNombreComercial());
                dd.setCantidad(det.getCantidad());
                dd.setPrecioUnitario(det.getPrecioUnitario());
                dd.setSubtotal(det.getSubtotal());
                return dd;
            }).toList());
        }
        return d;
    }
}

package edu.upn.clinica.backend.farmacia.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Venta {

    private Integer idVenta;
    private Integer idPaciente;
    private LocalDateTime fechaVenta;
    private BigDecimal total;
    private String estado;
    private String metodoPago;
    private String idPreferenciaMp;
    private String idPagoMp;
    private List<DetalleVenta> detalles;

    public Venta() {}

    public Integer getIdVenta() { return idVenta; }
    public void setIdVenta(Integer idVenta) { this.idVenta = idVenta; }

    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer idPaciente) { this.idPaciente = idPaciente; }

    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getIdPreferenciaMp() { return idPreferenciaMp; }
    public void setIdPreferenciaMp(String idPreferenciaMp) { this.idPreferenciaMp = idPreferenciaMp; }

    public String getIdPagoMp() { return idPagoMp; }
    public void setIdPagoMp(String idPagoMp) { this.idPagoMp = idPagoMp; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
}

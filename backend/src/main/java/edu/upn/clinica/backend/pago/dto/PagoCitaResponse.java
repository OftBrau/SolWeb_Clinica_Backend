package edu.upn.clinica.backend.pago.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoCitaResponse {

    private Integer       idPago;
    private Integer       idCita;
    private BigDecimal    monto;
    private String        metodoPago;
    private String        estadoPago;
    private String        referenciaMp;
    private String        urlPago;
    private LocalDateTime fechaPago;

    public PagoCitaResponse() {}

    public Integer       getIdPago() { return idPago; }
    public void          setIdPago(Integer idPago) { this.idPago = idPago; }

    public Integer       getIdCita() { return idCita; }
    public void          setIdCita(Integer idCita) { this.idCita = idCita; }

    public BigDecimal    getMonto() { return monto; }
    public void          setMonto(BigDecimal monto) { this.monto = monto; }

    public String        getMetodoPago() { return metodoPago; }
    public void          setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String        getEstadoPago() { return estadoPago; }
    public void          setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

    public String        getReferenciaMp() { return referenciaMp; }
    public void          setReferenciaMp(String referenciaMp) { this.referenciaMp = referenciaMp; }

    public String        getUrlPago() { return urlPago; }
    public void          setUrlPago(String urlPago) { this.urlPago = urlPago; }

    public LocalDateTime getFechaPago() { return fechaPago; }
    public void          setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
}

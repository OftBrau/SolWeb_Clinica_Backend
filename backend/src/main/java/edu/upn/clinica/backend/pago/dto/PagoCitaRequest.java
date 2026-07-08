package edu.upn.clinica.backend.pago.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class PagoCitaRequest {

    @NotNull
    private Integer idCita;

    @NotNull
    private BigDecimal monto;

    private String metodoPago;

    public PagoCitaRequest() {}

    public Integer    getIdCita() { return idCita; }
    public void       setIdCita(Integer idCita) { this.idCita = idCita; }

    public BigDecimal getMonto() { return monto; }
    public void       setMonto(BigDecimal monto) { this.monto = monto; }

    public String     getMetodoPago() { return metodoPago; }
    public void       setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
}

package edu.upn.clinica.backend.reserva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaEspecialistaRequest {

    @NotNull
    private Integer idEspecialidad;

    @NotNull
    private LocalDate fecha;

    @NotNull
    private LocalTime hora;

    @NotBlank
    private String motivo;

    private String metodoPago;

    private String tipo;

    public ReservaEspecialistaRequest() {}

    public Integer   getIdEspecialidad() { return idEspecialidad; }
    public void      setIdEspecialidad(Integer idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public LocalDate getFecha() { return fecha; }
    public void      setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void      setHora(LocalTime hora) { this.hora = hora; }

    public String    getMotivo() { return motivo; }
    public void      setMotivo(String motivo) { this.motivo = motivo; }

    public String    getMetodoPago() { return metodoPago; }
    public void      setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String    getTipo() { return tipo; }
    public void      setTipo(String tipo) { this.tipo = tipo; }
}

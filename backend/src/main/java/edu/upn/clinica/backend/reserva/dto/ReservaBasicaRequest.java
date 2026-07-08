package edu.upn.clinica.backend.reserva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaBasicaRequest {

    @NotNull
    private LocalDate fecha;

    @NotNull
    private LocalTime hora;

    @NotBlank
    private String motivo;

    private String tipo;

    public ReservaBasicaRequest() {}

    public LocalDate getFecha() { return fecha; }
    public void      setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void      setHora(LocalTime hora) { this.hora = hora; }

    public String    getMotivo() { return motivo; }
    public void      setMotivo(String motivo) { this.motivo = motivo; }

    public String    getTipo() { return tipo; }
    public void      setTipo(String tipo) { this.tipo = tipo; }
}

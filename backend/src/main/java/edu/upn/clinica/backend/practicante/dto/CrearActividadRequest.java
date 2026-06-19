package edu.upn.clinica.backend.practicante.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public class CrearActividadRequest {
    @NotNull
    private Integer idPracticante;

    @NotBlank
    private String titulo;

    private String descripcion;

    @NotBlank
    private String tipo;

    private LocalDate fecha;

    private LocalTime hora;

    private Integer idPaciente;

    public @NotNull Integer getIdPracticante() { return idPracticante; }
    public void setIdPracticante(@NotNull Integer v) { this.idPracticante = v; }

    public @NotBlank String getTitulo() { return titulo; }
    public void setTitulo(@NotBlank String v) { this.titulo = v; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String v) { this.descripcion = v; }

    public @NotBlank String getTipo() { return tipo; }
    public void setTipo(@NotBlank String v) { this.tipo = v; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate v) { this.fecha = v; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime v) { this.hora = v; }

    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer v) { this.idPaciente = v; }
}

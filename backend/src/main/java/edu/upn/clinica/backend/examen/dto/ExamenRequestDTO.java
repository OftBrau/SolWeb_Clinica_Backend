package edu.upn.clinica.backend.examen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExamenRequestDTO {

    @NotNull
    private Integer idConsulta;

    @NotNull
    private Integer idPaciente;

    @NotBlank
    private String tipo;

    @NotBlank
    private String nombreExamen;

    private String descripcion;

    public Integer getIdConsulta() { return idConsulta; }
    public void setIdConsulta(Integer v) { this.idConsulta = v; }

    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer v) { this.idPaciente = v; }

    public String getTipo() { return tipo; }
    public void setTipo(String v) { this.tipo = v; }

    public String getNombreExamen() { return nombreExamen; }
    public void setNombreExamen(String v) { this.nombreExamen = v; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String v) { this.descripcion = v; }
}

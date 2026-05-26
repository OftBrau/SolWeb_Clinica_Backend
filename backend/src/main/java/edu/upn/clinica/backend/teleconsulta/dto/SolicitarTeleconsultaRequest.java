package edu.upn.clinica.backend.teleconsulta.dto;

import jakarta.validation.constraints.NotBlank;

public class SolicitarTeleconsultaRequest {

    @NotBlank
    private String especialidad;

    @NotBlank
    private String medico;

    @NotBlank
    private String fecha;

    @NotBlank
    private String hora;

    private String motivo;

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String v) { this.especialidad = v; }

    public String getMedico() { return medico; }
    public void setMedico(String v) { this.medico = v; }

    public String getFecha() { return fecha; }
    public void setFecha(String v) { this.fecha = v; }

    public String getHora() { return hora; }
    public void setHora(String v) { this.hora = v; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String v) { this.motivo = v; }
}

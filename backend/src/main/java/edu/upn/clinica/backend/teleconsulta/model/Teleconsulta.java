package edu.upn.clinica.backend.teleconsulta.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Teleconsulta {

    private Integer idTeleconsulta;
    private Integer idPaciente;
    private Integer idDoctor;
    private String especialidad;
    private String urlSesion;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;
    private String motivo;
    private LocalDateTime createdAt;

    public Teleconsulta() {}

    public Integer getIdTeleconsulta() { return idTeleconsulta; }
    public void setIdTeleconsulta(Integer v) { this.idTeleconsulta = v; }

    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer v) { this.idPaciente = v; }

    public Integer getIdDoctor() { return idDoctor; }
    public void setIdDoctor(Integer v) { this.idDoctor = v; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String v) { this.especialidad = v; }

    public String getUrlSesion() { return urlSesion; }
    public void setUrlSesion(String v) { this.urlSesion = v; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate v) { this.fecha = v; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime v) { this.hora = v; }

    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String v) { this.motivo = v; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}

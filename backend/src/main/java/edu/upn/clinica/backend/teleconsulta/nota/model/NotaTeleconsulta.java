package edu.upn.clinica.backend.teleconsulta.nota.model;

import java.time.LocalDateTime;

public class NotaTeleconsulta {
    private Integer idNota;
    private Integer idTeleconsulta;
    private Integer idDoctor;
    private String contenido;
    private String tipo;
    private LocalDateTime createdAt;

    public NotaTeleconsulta() {}

    public Integer getIdNota() { return idNota; }
    public void setIdNota(Integer idNota) { this.idNota = idNota; }
    public Integer getIdTeleconsulta() { return idTeleconsulta; }
    public void setIdTeleconsulta(Integer idTeleconsulta) { this.idTeleconsulta = idTeleconsulta; }
    public Integer getIdDoctor() { return idDoctor; }
    public void setIdDoctor(Integer idDoctor) { this.idDoctor = idDoctor; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

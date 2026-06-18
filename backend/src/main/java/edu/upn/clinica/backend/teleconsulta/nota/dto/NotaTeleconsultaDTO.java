package edu.upn.clinica.backend.teleconsulta.nota.dto;

public class NotaTeleconsultaDTO {
    private Integer idNota;
    private Integer idTeleconsulta;
    private String doctor;
    private String contenido;
    private String tipo;
    private String createdAt;

    public NotaTeleconsultaDTO() {}

    public NotaTeleconsultaDTO(Integer idNota, Integer idTeleconsulta, String doctor, String contenido, String tipo, String createdAt) {
        this.idNota = idNota;
        this.idTeleconsulta = idTeleconsulta;
        this.doctor = doctor;
        this.contenido = contenido;
        this.tipo = tipo;
        this.createdAt = createdAt;
    }

    public Integer getIdNota() { return idNota; }
    public void setIdNota(Integer idNota) { this.idNota = idNota; }
    public Integer getIdTeleconsulta() { return idTeleconsulta; }
    public void setIdTeleconsulta(Integer idTeleconsulta) { this.idTeleconsulta = idTeleconsulta; }
    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

package edu.upn.clinica.backend.log.model;

import java.time.LocalDateTime;

public class LogActividad {
    private Integer idLog;
    private Integer idUsuario;
    private String email;
    private String accion;
    private String detalle;
    private String ip;
    private LocalDateTime createdAt;

    public LogActividad() {}

    public Integer getIdLog() { return idLog; }
    public void setIdLog(Integer v) { this.idLog = v; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer v) { this.idUsuario = v; }

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }

    public String getAccion() { return accion; }
    public void setAccion(String v) { this.accion = v; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String v) { this.detalle = v; }

    public String getIp() { return ip; }
    public void setIp(String v) { this.ip = v; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}

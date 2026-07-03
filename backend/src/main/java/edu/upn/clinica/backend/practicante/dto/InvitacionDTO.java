package edu.upn.clinica.backend.practicante.dto;

import java.time.LocalDateTime;

public class InvitacionDTO {
    private Integer idInvitacion;
    private Integer idDoctor;
    private Integer idPracticante;
    private String mensaje;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaRespuesta;
    private String nombreDoctor;
    private String nombrePracticante;
    private String especialidadDoctor;

    public Integer getIdInvitacion() { return idInvitacion; }
    public void setIdInvitacion(Integer idInvitacion) { this.idInvitacion = idInvitacion; }
    public Integer getIdDoctor() { return idDoctor; }
    public void setIdDoctor(Integer idDoctor) { this.idDoctor = idDoctor; }
    public Integer getIdPracticante() { return idPracticante; }
    public void setIdPracticante(Integer idPracticante) { this.idPracticante = idPracticante; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
    public String getNombreDoctor() { return nombreDoctor; }
    public void setNombreDoctor(String nombreDoctor) { this.nombreDoctor = nombreDoctor; }
    public String getNombrePracticante() { return nombrePracticante; }
    public void setNombrePracticante(String nombrePracticante) { this.nombrePracticante = nombrePracticante; }
    public String getEspecialidadDoctor() { return especialidadDoctor; }
    public void setEspecialidadDoctor(String especialidadDoctor) { this.especialidadDoctor = especialidadDoctor; }
}

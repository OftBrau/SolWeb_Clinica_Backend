package edu.upn.clinica.backend.enfermero.model;

import java.time.LocalDateTime;

public class AsignacionEnfermero {

    private Integer       idAsignacion;
    private Integer       idEnfermero;
    private Integer       idDoctor;
    private Boolean       activo;
    private LocalDateTime fechaAsignacion;
    private String        nombreEnfermero;
    private String        apellidoEnfermero;
    private String        emailEnfermero;
    private String        nombreDoctor;
    private String        apellidoDoctor;
    private String        especialidadDoctor;

    public AsignacionEnfermero() {}

    public Integer       getIdAsignacion() { return idAsignacion; }
    public void          setIdAsignacion(Integer idAsignacion) { this.idAsignacion = idAsignacion; }

    public Integer       getIdEnfermero() { return idEnfermero; }
    public void          setIdEnfermero(Integer idEnfermero) { this.idEnfermero = idEnfermero; }

    public Integer       getIdDoctor() { return idDoctor; }
    public void          setIdDoctor(Integer idDoctor) { this.idDoctor = idDoctor; }

    public Boolean       getActivo() { return activo; }
    public void          setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void          setFechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    public String        getNombreEnfermero() { return nombreEnfermero; }
    public void          setNombreEnfermero(String nombreEnfermero) { this.nombreEnfermero = nombreEnfermero; }

    public String        getApellidoEnfermero() { return apellidoEnfermero; }
    public void          setApellidoEnfermero(String apellidoEnfermero) { this.apellidoEnfermero = apellidoEnfermero; }

    public String        getEmailEnfermero() { return emailEnfermero; }
    public void          setEmailEnfermero(String emailEnfermero) { this.emailEnfermero = emailEnfermero; }

    public String        getNombreDoctor() { return nombreDoctor; }
    public void          setNombreDoctor(String nombreDoctor) { this.nombreDoctor = nombreDoctor; }

    public String        getApellidoDoctor() { return apellidoDoctor; }
    public void          setApellidoDoctor(String apellidoDoctor) { this.apellidoDoctor = apellidoDoctor; }

    public String        getEspecialidadDoctor() { return especialidadDoctor; }
    public void          setEspecialidadDoctor(String especialidadDoctor) { this.especialidadDoctor = especialidadDoctor; }
}

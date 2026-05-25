package edu.upn.clinica.backend.doctor.model;

public class Doctor {
    private Integer idDoctor;
    private Integer idUsuario;
    private String especialidad;

    public Doctor() {}

    public Integer getIdDoctor() { return idDoctor; }
    public void setIdDoctor(Integer v) { this.idDoctor = v; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer v) { this.idUsuario = v; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String v) { this.especialidad = v; }
}

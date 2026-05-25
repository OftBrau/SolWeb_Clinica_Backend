package edu.upn.clinica.backend.doctor.dto;

public class DoctorDTO {
    private Integer idDoctor;
    private String nombre;
    private String apellido;
    private String email;
    private String especialidad;
    private String telefono;
    private String estado;

    public DoctorDTO() {}

    public Integer getIdDoctor() { return idDoctor; }
    public void setIdDoctor(Integer v) { this.idDoctor = v; }

    public String getNombre() { return nombre; }
    public void setNombre(String v) { this.nombre = v; }

    public String getApellido() { return apellido; }
    public void setApellido(String v) { this.apellido = v; }

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String v) { this.especialidad = v; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String v) { this.telefono = v; }

    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }
}

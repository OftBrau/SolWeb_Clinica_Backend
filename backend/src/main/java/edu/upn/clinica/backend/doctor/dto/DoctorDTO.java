package edu.upn.clinica.backend.doctor.dto;

public class DoctorDTO {
    private Integer idDoctor;
    private String nombre;
    private String apellido;
    private String email;
    private String especialidad;
    private String telefono;
    private String cmp;
    private String fotoUrl;
    private String estado;
    private Boolean destacado;

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

    public String getCmp() { return cmp; }
    public void setCmp(String v) { this.cmp = v; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String v) { this.fotoUrl = v; }

    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }

    public Boolean getDestacado() { return destacado; }
    public void setDestacado(Boolean v) { this.destacado = v; }
}

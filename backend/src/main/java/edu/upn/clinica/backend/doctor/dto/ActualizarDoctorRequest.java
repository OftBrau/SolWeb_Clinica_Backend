package edu.upn.clinica.backend.doctor.dto;

import jakarta.validation.constraints.NotBlank;

public class ActualizarDoctorRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    private String telefono;

    @NotBlank
    private String especialidad;

    private String cmp;

    public @NotBlank String getNombre() { return nombre; }
    public void setNombre(@NotBlank String nombre) { this.nombre = nombre; }

    public @NotBlank String getApellido() { return apellido; }
    public void setApellido(@NotBlank String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public @NotBlank String getEspecialidad() { return especialidad; }
    public void setEspecialidad(@NotBlank String especialidad) { this.especialidad = especialidad; }

    public String getCmp() { return cmp; }
    public void setCmp(String cmp) { this.cmp = cmp; }
}

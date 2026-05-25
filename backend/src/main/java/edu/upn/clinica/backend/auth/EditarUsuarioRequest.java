package edu.upn.clinica.backend.auth;

import jakarta.validation.constraints.NotBlank;

public class EditarUsuarioRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    private String telefono;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}

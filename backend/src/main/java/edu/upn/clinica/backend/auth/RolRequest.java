package edu.upn.clinica.backend.auth;

import jakarta.validation.constraints.NotBlank;

public class RolRequest {

    @NotBlank
    private String rol;

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}

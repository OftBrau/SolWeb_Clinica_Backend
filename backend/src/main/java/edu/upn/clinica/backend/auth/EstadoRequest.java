package edu.upn.clinica.backend.auth;

import jakarta.validation.constraints.NotBlank;

public class EstadoRequest {

    @NotBlank
    private String estado;

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

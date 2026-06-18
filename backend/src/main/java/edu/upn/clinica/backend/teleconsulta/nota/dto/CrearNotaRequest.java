package edu.upn.clinica.backend.teleconsulta.nota.dto;

import jakarta.validation.constraints.NotBlank;

public class CrearNotaRequest {
    @NotBlank
    private String contenido;

    @NotBlank
    private String tipo;

    public @NotBlank String getContenido() { return contenido; }
    public void setContenido(@NotBlank String contenido) { this.contenido = contenido; }

    public @NotBlank String getTipo() { return tipo; }
    public void setTipo(@NotBlank String tipo) { this.tipo = tipo; }
}

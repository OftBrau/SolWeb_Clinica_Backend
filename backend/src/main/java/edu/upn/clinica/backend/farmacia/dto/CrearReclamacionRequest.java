package edu.upn.clinica.backend.farmacia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CrearReclamacionRequest {

    @NotBlank
    private String nombreCompleto;
    private String email;
    private String telefono;
    @NotNull
    private String tipo;
    @NotBlank
    private String descripcion;
    private String productoServicio;

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getProductoServicio() { return productoServicio; }
    public void setProductoServicio(String productoServicio) { this.productoServicio = productoServicio; }
}

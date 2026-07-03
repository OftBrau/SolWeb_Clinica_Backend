package edu.upn.clinica.backend.farmacia.dto;

import java.time.LocalDateTime;

public class ReclamacionDTO {

    private Integer idReclamacion;
    private Integer idPaciente;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String tipo;
    private String descripcion;
    private String productoServicio;
    private String estado;
    private String respuesta;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaRespuesta;

    public Integer getIdReclamacion() { return idReclamacion; }
    public void setIdReclamacion(Integer idReclamacion) { this.idReclamacion = idReclamacion; }

    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer idPaciente) { this.idPaciente = idPaciente; }

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

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
}

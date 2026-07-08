package edu.upn.clinica.backend.especialidad.model;

import java.math.BigDecimal;

public class Especialidad {
    private Integer    idEspecialidad;
    private String     nombre;
    private String     descripcion;
    private String     estado;
    private BigDecimal costoExtra;

    public Especialidad() {}

    public Integer    getIdEspecialidad() { return idEspecialidad; }
    public void       setIdEspecialidad(Integer id) { this.idEspecialidad = id; }

    public String     getNombre() { return nombre; }
    public void       setNombre(String nombre) { this.nombre = nombre; }

    public String     getDescripcion() { return descripcion; }
    public void       setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String     getEstado() { return estado; }
    public void       setEstado(String estado) { this.estado = estado; }

    public BigDecimal getCostoExtra() { return costoExtra; }
    public void       setCostoExtra(BigDecimal costoExtra) { this.costoExtra = costoExtra; }
}

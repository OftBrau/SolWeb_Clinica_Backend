package edu.upn.clinica.backend.especialidad.model;

public class Especialidad {
    private Integer idEspecialidad;
    private String nombre;
    private String descripcion;
    private String estado;

    public Especialidad() {}

    public Integer getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(Integer id) { this.idEspecialidad = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

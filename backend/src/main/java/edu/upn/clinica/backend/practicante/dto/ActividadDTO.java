package edu.upn.clinica.backend.practicante.dto;

public class ActividadDTO {
    private Integer idActividad;
    private String titulo;
    private String descripcion;
    private String tipo;
    private String fecha;
    private String hora;
    private String estado;
    private String paciente;
    private String supervisor;

    public ActividadDTO() {}

    public Integer getIdActividad() { return idActividad; }
    public void setIdActividad(Integer v) { this.idActividad = v; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String v) { this.titulo = v; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String v) { this.descripcion = v; }

    public String getTipo() { return tipo; }
    public void setTipo(String v) { this.tipo = v; }

    public String getFecha() { return fecha; }
    public void setFecha(String v) { this.fecha = v; }

    public String getHora() { return hora; }
    public void setHora(String v) { this.hora = v; }

    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }

    public String getPaciente() { return paciente; }
    public void setPaciente(String v) { this.paciente = v; }

    public String getSupervisor() { return supervisor; }
    public void setSupervisor(String v) { this.supervisor = v; }
}

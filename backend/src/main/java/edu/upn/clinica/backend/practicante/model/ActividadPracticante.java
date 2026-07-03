package edu.upn.clinica.backend.practicante.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class ActividadPracticante {
    private Integer idActividad;
    private Integer idPracticante;
    private String titulo;
    private String descripcion;
    private String tipo;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado;
    private Integer idPaciente;
    private Integer idSupervisor;
    private Integer idCita;
    private Integer idTeleconsulta;

    public ActividadPracticante() {}

    public Integer getIdActividad() { return idActividad; }
    public void setIdActividad(Integer v) { this.idActividad = v; }

    public Integer getIdPracticante() { return idPracticante; }
    public void setIdPracticante(Integer v) { this.idPracticante = v; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String v) { this.titulo = v; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String v) { this.descripcion = v; }

    public String getTipo() { return tipo; }
    public void setTipo(String v) { this.tipo = v; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate v) { this.fecha = v; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime v) { this.hora = v; }

    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }

    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer v) { this.idPaciente = v; }

    public Integer getIdSupervisor() { return idSupervisor; }
    public void setIdSupervisor(Integer v) { this.idSupervisor = v; }

    public Integer getIdCita() { return idCita; }
    public void setIdCita(Integer v) { this.idCita = v; }

    public Integer getIdTeleconsulta() { return idTeleconsulta; }
    public void setIdTeleconsulta(Integer v) { this.idTeleconsulta = v; }
}

package edu.upn.clinica.backend.practicante.model;

import java.time.LocalDate;

public class EvaluacionPracticante {
    private Integer idEvaluacion;
    private Integer idPracticante;
    private Integer idSupervisor;
    private LocalDate fecha;
    private Double puntuacion;
    private String comentario;

    public EvaluacionPracticante() {}

    public Integer getIdEvaluacion() { return idEvaluacion; }
    public void setIdEvaluacion(Integer v) { this.idEvaluacion = v; }

    public Integer getIdPracticante() { return idPracticante; }
    public void setIdPracticante(Integer v) { this.idPracticante = v; }

    public Integer getIdSupervisor() { return idSupervisor; }
    public void setIdSupervisor(Integer v) { this.idSupervisor = v; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate v) { this.fecha = v; }

    public Double getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Double v) { this.puntuacion = v; }

    public String getComentario() { return comentario; }
    public void setComentario(String v) { this.comentario = v; }
}

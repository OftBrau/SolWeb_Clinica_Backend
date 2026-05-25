package edu.upn.clinica.backend.practicante.dto;

public class EvaluacionDTO {
    private Integer idEvaluacion;
    private String fecha;
    private Double puntuacion;
    private String comentario;
    private String supervisor;

    public EvaluacionDTO() {}

    public Integer getIdEvaluacion() { return idEvaluacion; }
    public void setIdEvaluacion(Integer v) { this.idEvaluacion = v; }

    public String getFecha() { return fecha; }
    public void setFecha(String v) { this.fecha = v; }

    public Double getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Double v) { this.puntuacion = v; }

    public String getComentario() { return comentario; }
    public void setComentario(String v) { this.comentario = v; }

    public String getSupervisor() { return supervisor; }
    public void setSupervisor(String v) { this.supervisor = v; }
}

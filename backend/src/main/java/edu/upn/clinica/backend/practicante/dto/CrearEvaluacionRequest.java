package edu.upn.clinica.backend.practicante.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class CrearEvaluacionRequest {
    @NotNull
    private Integer idPracticante;

    @NotNull
    @DecimalMin("0.0") @DecimalMax("10.0")
    private Double puntuacion;

    private String comentario;

    public @NotNull Integer getIdPracticante() { return idPracticante; }
    public void setIdPracticante(@NotNull Integer v) { this.idPracticante = v; }

    public @NotNull @DecimalMin("0.0") @DecimalMax("10.0") Double getPuntuacion() { return puntuacion; }
    public void setPuntuacion(@NotNull @DecimalMin("0.0") @DecimalMax("10.0") Double v) { this.puntuacion = v; }

    public String getComentario() { return comentario; }
    public void setComentario(String v) { this.comentario = v; }
}

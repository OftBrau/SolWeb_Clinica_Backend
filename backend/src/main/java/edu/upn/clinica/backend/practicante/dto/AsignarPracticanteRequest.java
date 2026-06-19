package edu.upn.clinica.backend.practicante.dto;

import jakarta.validation.constraints.NotNull;

public class AsignarPracticanteRequest {
    @NotNull
    private Integer idPracticante;

    @NotNull
    private Integer idSupervisor;

    public @NotNull Integer getIdPracticante() { return idPracticante; }
    public void setIdPracticante(@NotNull Integer v) { this.idPracticante = v; }

    public @NotNull Integer getIdSupervisor() { return idSupervisor; }
    public void setIdSupervisor(@NotNull Integer v) { this.idSupervisor = v; }
}

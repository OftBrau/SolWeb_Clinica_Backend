package edu.upn.clinica.backend.doctor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DisponibilidadDTO {
    private Integer idDisponibilidad;

    @NotNull
    private Integer idDoctor;

    @NotBlank
    private String diaSemana;

    @NotBlank
    private String horaInicio;

    @NotBlank
    private String horaFin;

    public DisponibilidadDTO() {}

    public DisponibilidadDTO(Integer idDisponibilidad, Integer idDoctor, String diaSemana, String horaInicio, String horaFin) {
        this.idDisponibilidad = idDisponibilidad;
        this.idDoctor = idDoctor;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public Integer getIdDisponibilidad() { return idDisponibilidad; }
    public void setIdDisponibilidad(Integer v) { this.idDisponibilidad = v; }

    public Integer getIdDoctor() { return idDoctor; }
    public void setIdDoctor(Integer v) { this.idDoctor = v; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String v) { this.diaSemana = v; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String v) { this.horaInicio = v; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String v) { this.horaFin = v; }
}

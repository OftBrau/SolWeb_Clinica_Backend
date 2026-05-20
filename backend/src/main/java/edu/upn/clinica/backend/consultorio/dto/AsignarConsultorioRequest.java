package edu.upn.clinica.backend.consultorio.dto;

import jakarta.validation.constraints.NotNull;

public class AsignarConsultorioRequest {
    @NotNull
    private Integer idConsultorio;

    @NotNull
    private Integer idDoctor;

    private String diaSemana;
    private String horaInicio;
    private String horaFin;

    public Integer getIdConsultorio() { return idConsultorio; }
    public void setIdConsultorio(Integer id) { this.idConsultorio = id; }

    public Integer getIdDoctor() { return idDoctor; }
    public void setIdDoctor(Integer id) { this.idDoctor = id; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }
}

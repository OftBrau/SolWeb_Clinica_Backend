package edu.upn.clinica.backend.horario.model;

import java.time.LocalTime;

public class HorarioAtencion {
    private Integer idHorario;
    private Integer idEspecialidad;
    private String diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public HorarioAtencion() {}

    public Integer getIdHorario() { return idHorario; }
    public void setIdHorario(Integer id) { this.idHorario = id; }

    public Integer getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(Integer id) { this.idEspecialidad = id; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime t) { this.horaInicio = t; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime t) { this.horaFin = t; }
}

package edu.upn.clinica.backend.horario.dto;

import edu.upn.clinica.backend.horario.model.HorarioAtencion;

public class HorarioDTO {
    private Integer idHorario;
    private Integer idEspecialidad;
    private String especialidadNombre;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;

    public HorarioDTO() {}

    public HorarioDTO(HorarioAtencion h) {
        this.idHorario = h.getIdHorario();
        this.idEspecialidad = h.getIdEspecialidad();
        this.diaSemana = h.getDiaSemana();
        this.horaInicio = h.getHoraInicio() != null ? h.getHoraInicio().toString() : null;
        this.horaFin = h.getHoraFin() != null ? h.getHoraFin().toString() : null;
    }

    public Integer getIdHorario() { return idHorario; }
    public void setIdHorario(Integer id) { this.idHorario = id; }

    public Integer getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(Integer id) { this.idEspecialidad = id; }

    public String getEspecialidadNombre() { return especialidadNombre; }
    public void setEspecialidadNombre(String v) { this.especialidadNombre = v; }

    public String getDiaSemana() { return diaSemana; }
    public void setDiaSemana(String v) { this.diaSemana = v; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String v) { this.horaInicio = v; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String v) { this.horaFin = v; }
}

package edu.upn.clinica.backend.teleconsulta.dto;

public class TeleconsultaDTO {

    private Integer idTeleconsulta;
    private String paciente;
    private String medico;
    private String especialidad;
    private String fecha;
    private String hora;
    private String estado;
    private String linkSala;
    private String motivo;

    public TeleconsultaDTO() {}

    public TeleconsultaDTO(Integer idTeleconsulta, String paciente, String medico,
                           String especialidad, String fecha, String hora,
                           String estado, String linkSala, String motivo) {
        this.idTeleconsulta = idTeleconsulta;
        this.paciente = paciente;
        this.medico = medico;
        this.especialidad = especialidad;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.linkSala = linkSala;
        this.motivo = motivo;
    }

    public Integer getIdTeleconsulta() { return idTeleconsulta; }
    public void setIdTeleconsulta(Integer v) { this.idTeleconsulta = v; }

    public String getPaciente() { return paciente; }
    public void setPaciente(String v) { this.paciente = v; }

    public String getMedico() { return medico; }
    public void setMedico(String v) { this.medico = v; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String v) { this.especialidad = v; }

    public String getFecha() { return fecha; }
    public void setFecha(String v) { this.fecha = v; }

    public String getHora() { return hora; }
    public void setHora(String v) { this.hora = v; }

    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }

    public String getLinkSala() { return linkSala; }
    public void setLinkSala(String v) { this.linkSala = v; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String v) { this.motivo = v; }
}

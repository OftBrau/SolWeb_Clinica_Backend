package edu.upn.clinica.backend.practicante.dto;

public class ConsultaPracDTO {
    private Integer idConsulta;
    private Integer idCita;
    private Integer idPaciente;
    private String paciente;
    private String motivo;
    private String diagnostico;
    private String receta;
    private String estado;
    private String fecha;
    private String supervisor;

    public ConsultaPracDTO() {}

    public Integer getIdConsulta() { return idConsulta; }
    public void setIdConsulta(Integer v) { this.idConsulta = v; }

    public Integer getIdCita() { return idCita; }
    public void setIdCita(Integer v) { this.idCita = v; }

    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer v) { this.idPaciente = v; }

    public String getPaciente() { return paciente; }
    public void setPaciente(String v) { this.paciente = v; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String v) { this.motivo = v; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String v) { this.diagnostico = v; }

    public String getReceta() { return receta; }
    public void setReceta(String v) { this.receta = v; }

    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }

    public String getFecha() { return fecha; }
    public void setFecha(String v) { this.fecha = v; }

    public String getSupervisor() { return supervisor; }
    public void setSupervisor(String v) { this.supervisor = v; }
}

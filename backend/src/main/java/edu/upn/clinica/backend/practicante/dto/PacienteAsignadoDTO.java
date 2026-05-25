package edu.upn.clinica.backend.practicante.dto;

public class PacienteAsignadoDTO {
    private Integer idPaciente;
    private String nombreCompleto;
    private String codigoEstudiante;
    private String ultimaConsulta;

    public PacienteAsignadoDTO() {}

    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer v) { this.idPaciente = v; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String v) { this.nombreCompleto = v; }

    public String getCodigoEstudiante() { return codigoEstudiante; }
    public void setCodigoEstudiante(String v) { this.codigoEstudiante = v; }

    public String getUltimaConsulta() { return ultimaConsulta; }
    public void setUltimaConsulta(String v) { this.ultimaConsulta = v; }
}

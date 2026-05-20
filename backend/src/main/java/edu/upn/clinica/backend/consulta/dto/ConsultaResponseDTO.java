package edu.upn.clinica.backend.consulta.dto;

import edu.upn.clinica.backend.consulta.model.Consulta;

public class ConsultaResponseDTO {

    private Integer idConsulta;
    private Integer idCita;
    private Integer idPaciente;
    private String  paciente;
    private Integer idDoctor;
    private String  doctor;
    private String  diagnosticoCie10;
    private String  descripcionDiagnostico;
    private String  tratamiento;
    private String  prescripcion;
    private String  createdAt;

    public ConsultaResponseDTO() {}

    public ConsultaResponseDTO(Consulta c, String nombrePaciente, String nombreDoctor) {
        this.idConsulta = c.getIdConsulta();
        this.idCita = c.getIdCita();
        this.idPaciente = c.getIdPaciente();
        this.paciente = nombrePaciente;
        this.idDoctor = c.getIdDoctor();
        this.doctor = nombreDoctor;
        this.diagnosticoCie10 = c.getDiagnosticoCie10();
        this.descripcionDiagnostico = c.getDescripcionDiagnostico();
        this.tratamiento = c.getTratamiento();
        this.prescripcion = c.getPrescripcion();
        this.createdAt = c.getCreatedAt() != null ? c.getCreatedAt().toString() : null;
    }

    public Integer getIdConsulta()                { return idConsulta; }
    public void setIdConsulta(Integer v)         { this.idConsulta = v; }

    public Integer getIdCita()                   { return idCita; }
    public void setIdCita(Integer v)             { this.idCita = v; }

    public Integer getIdPaciente()               { return idPaciente; }
    public void setIdPaciente(Integer v)         { this.idPaciente = v; }

    public String getPaciente()                  { return paciente; }
    public void setPaciente(String v)            { this.paciente = v; }

    public Integer getIdDoctor()                 { return idDoctor; }
    public void setIdDoctor(Integer v)           { this.idDoctor = v; }

    public String getDoctor()                    { return doctor; }
    public void setDoctor(String v)              { this.doctor = v; }

    public String getDiagnosticoCie10()          { return diagnosticoCie10; }
    public void setDiagnosticoCie10(String v)   { this.diagnosticoCie10 = v; }

    public String getDescripcionDiagnostico()   { return descripcionDiagnostico; }
    public void setDescripcionDiagnostico(String v){ this.descripcionDiagnostico = v; }

    public String getTratamiento()              { return tratamiento; }
    public void setTratamiento(String v)        { this.tratamiento = v; }

    public String getPrescripcion()             { return prescripcion; }
    public void setPrescripcion(String v)       { this.prescripcion = v; }

    public String getCreatedAt()                { return createdAt; }
    public void setCreatedAt(String v)          { this.createdAt = v; }
}

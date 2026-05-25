package edu.upn.clinica.backend.consulta.model;

import java.time.LocalDateTime;

public class Consulta {

    private Integer idConsulta;
    private Integer idCita;
    private Integer idPaciente;
    private Integer idDoctor;
    private String  diagnosticoCie10;
    private String  descripcionDiagnostico;
    private String  tratamiento;
    private String  prescripcion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Consulta() {}

    public Integer getIdConsulta()                 { return idConsulta; }
    public void setIdConsulta(Integer v)          { this.idConsulta = v; }

    public Integer getIdCita()                    { return idCita; }
    public void setIdCita(Integer v)              { this.idCita = v; }

    public Integer getIdPaciente()                { return idPaciente; }
    public void setIdPaciente(Integer v)          { this.idPaciente = v; }

    public Integer getIdDoctor()                  { return idDoctor; }
    public void setIdDoctor(Integer v)            { this.idDoctor = v; }

    public String getDiagnosticoCie10()           { return diagnosticoCie10; }
    public void setDiagnosticoCie10(String v)     { this.diagnosticoCie10 = v; }

    public String getDescripcionDiagnostico()     { return descripcionDiagnostico; }
    public void setDescripcionDiagnostico(String v){ this.descripcionDiagnostico = v; }

    public String getTratamiento()                { return tratamiento; }
    public void setTratamiento(String v)          { this.tratamiento = v; }

    public String getPrescripcion()               { return prescripcion; }
    public void setPrescripcion(String v)         { this.prescripcion = v; }

    public LocalDateTime getCreatedAt()           { return createdAt; }
    public void setCreatedAt(LocalDateTime v)    { this.createdAt = v; }

    public LocalDateTime getUpdatedAt()           { return updatedAt; }
    public void setUpdatedAt(LocalDateTime v)    { this.updatedAt = v; }
}

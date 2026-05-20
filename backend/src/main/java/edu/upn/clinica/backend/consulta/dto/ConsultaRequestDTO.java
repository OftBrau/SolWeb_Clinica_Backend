package edu.upn.clinica.backend.consulta.dto;

import jakarta.validation.constraints.NotNull;

public class ConsultaRequestDTO {

    @NotNull
    private Integer idCita;

    private String diagnosticoCie10;
    private String descripcionDiagnostico;
    private String tratamiento;
    private String prescripcion;

    public Integer getIdCita()                { return idCita; }
    public void setIdCita(Integer v)         { this.idCita = v; }

    public String getDiagnosticoCie10()       { return diagnosticoCie10; }
    public void setDiagnosticoCie10(String v){ this.diagnosticoCie10 = v; }

    public String getDescripcionDiagnostico(){ return descripcionDiagnostico; }
    public void setDescripcionDiagnostico(String v){ this.descripcionDiagnostico = v; }

    public String getTratamiento()           { return tratamiento; }
    public void setTratamiento(String v)     { this.tratamiento = v; }

    public String getPrescripcion()          { return prescripcion; }
    public void setPrescripcion(String v)    { this.prescripcion = v; }
}

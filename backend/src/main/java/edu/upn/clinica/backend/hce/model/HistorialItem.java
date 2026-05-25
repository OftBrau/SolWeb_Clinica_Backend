package edu.upn.clinica.backend.hce.model;

import java.time.LocalDateTime;

public class HistorialItem {
    private Integer idConsulta;
    private Integer idPaciente;
    private String  nombrePaciente;
    private String  codigoEstudiante;
    private LocalDateTime fecha;
    private String  diagnosticoCie10;
    private String  descripcionDiag;
    private String  tratamiento;
    private String  prescripcion;
    private String  nombreDoctor;
    private String  especialidad;

    public HistorialItem() {}

    public Integer getIdConsulta() { return idConsulta; }
    public void setIdConsulta(Integer v) { this.idConsulta = v; }

    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer v) { this.idPaciente = v; }

    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String v) { this.nombrePaciente = v; }

    public String getCodigoEstudiante() { return codigoEstudiante; }
    public void setCodigoEstudiante(String v) { this.codigoEstudiante = v; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime v) { this.fecha = v; }

    public String getDiagnosticoCie10() { return diagnosticoCie10; }
    public void setDiagnosticoCie10(String v) { this.diagnosticoCie10 = v; }

    public String getDescripcionDiag() { return descripcionDiag; }
    public void setDescripcionDiag(String v) { this.descripcionDiag = v; }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String v) { this.tratamiento = v; }

    public String getPrescripcion() { return prescripcion; }
    public void setPrescripcion(String v) { this.prescripcion = v; }

    public String getNombreDoctor() { return nombreDoctor; }
    public void setNombreDoctor(String v) { this.nombreDoctor = v; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String v) { this.especialidad = v; }
}
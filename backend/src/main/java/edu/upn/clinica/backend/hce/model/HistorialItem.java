package edu.upn.clinica.backend.hce.model;

import java.time.LocalDateTime;

public class HistorialItem {
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

    // Getters y Setters
    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer idPaciente) { this.idPaciente = idPaciente; }
    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }
    public String getCodigoEstudiante() { return codigoEstudiante; }
    public void setCodigoEstudiante(String codigoEstudiante) { this.codigoEstudiante = codigoEstudiante; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getDiagnosticoCie10() { return diagnosticoCie10; }
    public void setDiagnosticoCie10(String diagnosticoCie10) { this.diagnosticoCie10 = diagnosticoCie10; }
    public String getDescripcionDiag() { return descripcionDiag; }
    public void setDescripcionDiag(String descripcionDiag) { this.descripcionDiag = descripcionDiag; }
    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }
    public String getPrescripcion() { return prescripcion; }
    public void setPrescripcion(String prescripcion) { this.prescripcion = prescripcion; }
    public String getNombreDoctor() { return nombreDoctor; }
    public void setNombreDoctor(String nombreDoctor) { this.nombreDoctor = nombreDoctor; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
}
package edu.upn.clinica.backend.examen.dto;

import java.time.LocalDateTime;

public class ExamenResponseDTO {
    private Integer idExamen;
    private Integer idConsulta;
    private Integer idPaciente;
    private String nombrePaciente;
    private Integer idDoctor;
    private String nombreDoctor;
    private String tipo;
    private String nombreExamen;
    private String descripcion;
    private String resultado;
    private String estado;
    private LocalDateTime createdAt;

    public ExamenResponseDTO() {}

    public Integer getIdExamen() { return idExamen; }
    public void setIdExamen(Integer v) { this.idExamen = v; }

    public Integer getIdConsulta() { return idConsulta; }
    public void setIdConsulta(Integer v) { this.idConsulta = v; }

    public Integer getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Integer v) { this.idPaciente = v; }

    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String v) { this.nombrePaciente = v; }

    public Integer getIdDoctor() { return idDoctor; }
    public void setIdDoctor(Integer v) { this.idDoctor = v; }

    public String getNombreDoctor() { return nombreDoctor; }
    public void setNombreDoctor(String v) { this.nombreDoctor = v; }

    public String getTipo() { return tipo; }
    public void setTipo(String v) { this.tipo = v; }

    public String getNombreExamen() { return nombreExamen; }
    public void setNombreExamen(String v) { this.nombreExamen = v; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String v) { this.descripcion = v; }

    public String getResultado() { return resultado; }
    public void setResultado(String v) { this.resultado = v; }

    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
}

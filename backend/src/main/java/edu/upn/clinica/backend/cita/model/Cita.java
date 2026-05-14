package edu.upn.clinica.backend.cita.model;

import java.time.LocalDate;
import java.time.LocalTime;

// ============================================================
//  Cita.java
//  Modelo del dominio — mapea la tabla: citas
// ============================================================
public class Cita {

    private Integer   idCita;
    private Integer   idPaciente;
    private Integer   idDoctor;
    private Integer   idConsultorio;
    private LocalDate fecha;
    private LocalTime hora;
    private String    estado;   // CONFIRMADA | EN_ATENCION | ATENDIDA | CANCELADA | NO_ASISTIO
    private String    tipo;     // PRESENCIAL | TELECONSULTA
    private String    motivo;

    public Cita() {}

    public Integer   getIdCita()                              { return idCita; }
    public void      setIdCita(Integer idCita)               { this.idCita = idCita; }

    public Integer   getIdPaciente()                          { return idPaciente; }
    public void      setIdPaciente(Integer idPaciente)       { this.idPaciente = idPaciente; }

    public Integer   getIdDoctor()                            { return idDoctor; }
    public void      setIdDoctor(Integer idDoctor)           { this.idDoctor = idDoctor; }

    public Integer   getIdConsultorio()                       { return idConsultorio; }
    public void      setIdConsultorio(Integer id)            { this.idConsultorio = id; }

    public LocalDate getFecha()                               { return fecha; }
    public void      setFecha(LocalDate fecha)               { this.fecha = fecha; }

    public LocalTime getHora()                                { return hora; }
    public void      setHora(LocalTime hora)                 { this.hora = hora; }

    public String    getEstado()                              { return estado; }
    public void      setEstado(String estado)                { this.estado = estado; }

    public String    getTipo()                                { return tipo; }
    public void      setTipo(String tipo)                    { this.tipo = tipo; }

    public String    getMotivo()                              { return motivo; }
    public void      setMotivo(String motivo)                { this.motivo = motivo; }
}
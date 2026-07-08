package edu.upn.clinica.backend.cita.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class Cita {

    private Integer    idCita;
    private Integer    idPaciente;
    private Integer    idDoctor;
    private Integer    idConsultorio;
    private String     consultorio;
    private LocalDate  fecha;
    private LocalTime  hora;
    private String     estado;
    private String     tipo;
    private String     motivo;
    private String     tipoReserva;
    private Integer    idEspecialidad;
    private Integer    idAsistente;
    private Integer    idEnfermero;
    private BigDecimal montoExtra;

    public Cita() {}

    public Integer    getIdCita()                              { return idCita; }
    public void       setIdCita(Integer idCita)               { this.idCita = idCita; }

    public Integer    getIdPaciente()                          { return idPaciente; }
    public void       setIdPaciente(Integer idPaciente)       { this.idPaciente = idPaciente; }

    public Integer    getIdDoctor()                            { return idDoctor; }
    public void       setIdDoctor(Integer idDoctor)           { this.idDoctor = idDoctor; }

    public Integer    getIdConsultorio()                       { return idConsultorio; }
    public void       setIdConsultorio(Integer id)            { this.idConsultorio = id; }
    public String     getConsultorio()                          { return consultorio; }
    public void       setConsultorio(String name)               { this.consultorio = name; }

    public LocalDate  getFecha()                               { return fecha; }
    public void       setFecha(LocalDate fecha)               { this.fecha = fecha; }

    public LocalTime  getHora()                                { return hora; }
    public void       setHora(LocalTime hora)                 { this.hora = hora; }

    public String     getEstado()                              { return estado; }
    public void       setEstado(String estado)                { this.estado = estado; }

    public String     getTipo()                                { return tipo; }
    public void       setTipo(String tipo)                    { this.tipo = tipo; }

    public String     getMotivo()                              { return motivo; }
    public void       setMotivo(String motivo)                { this.motivo = motivo; }

    public String     getTipoReserva()                         { return tipoReserva; }
    public void       setTipoReserva(String tipoReserva)      { this.tipoReserva = tipoReserva; }

    public Integer    getIdEspecialidad()                      { return idEspecialidad; }
    public void       setIdEspecialidad(Integer idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public Integer    getIdAsistente()                         { return idAsistente; }
    public void       setIdAsistente(Integer idAsistente)     { this.idAsistente = idAsistente; }

    public Integer    getIdEnfermero()                         { return idEnfermero; }
    public void       setIdEnfermero(Integer idEnfermero)     { this.idEnfermero = idEnfermero; }

    public BigDecimal getMontoExtra()                          { return montoExtra; }
    public void       setMontoExtra(BigDecimal montoExtra)    { this.montoExtra = montoExtra; }
}
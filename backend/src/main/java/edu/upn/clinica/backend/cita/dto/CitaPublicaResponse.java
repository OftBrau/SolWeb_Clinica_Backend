package edu.upn.clinica.backend.cita.dto;

import java.math.BigDecimal;

public class CitaPublicaResponse {

    private Integer    idCita;
    private String     paciente;
    private String     doctor;
    private String     especialidad;
    private String     fecha;
    private String     hora;
    private String     estado;
    private String     tipo;
    private String     linkSala;
    private String     consultorio;
    private String     tipoReserva;
    private BigDecimal montoExtra;
    private Integer    idPaciente;
    private Integer    idDoctor;

    public CitaPublicaResponse() {}

    public CitaPublicaResponse(Integer idCita, String paciente, String doctor,
                                String especialidad, String fecha, String hora,
                                String estado, String tipo) {
        this(idCita, paciente, doctor, especialidad, fecha, hora, estado, tipo, null, null);
    }

    public CitaPublicaResponse(Integer idCita, String paciente, String doctor,
                                String especialidad, String fecha, String hora,
                                String estado, String tipo, String linkSala) {
        this(idCita, paciente, doctor, especialidad, fecha, hora, estado, tipo, linkSala, null);
    }

    public CitaPublicaResponse(Integer idCita, String paciente, String doctor,
                                String especialidad, String fecha, String hora,
                                String estado, String tipo, String linkSala, String consultorio) {
        this.idCita       = idCita;
        this.paciente     = paciente;
        this.doctor       = doctor;
        this.especialidad = especialidad;
        this.fecha        = fecha;
        this.hora         = hora;
        this.estado       = estado;
        this.tipo         = tipo;
        this.linkSala     = linkSala;
        this.consultorio  = consultorio;
    }

    public Integer    getIdCita()                            { return idCita; }
    public void       setIdCita(Integer idCita)             { this.idCita = idCita; }

    public String     getPaciente()                          { return paciente; }
    public void       setPaciente(String paciente)          { this.paciente = paciente; }

    public String     getDoctor()                            { return doctor; }
    public void       setDoctor(String doctor)              { this.doctor = doctor; }

    public String     getEspecialidad()                      { return especialidad; }
    public void       setEspecialidad(String especialidad)  { this.especialidad = especialidad; }

    public String     getFecha()                             { return fecha; }
    public void       setFecha(String fecha)                { this.fecha = fecha; }

    public String     getHora()                              { return hora; }
    public void       setHora(String hora)                  { this.hora = hora; }

    public String     getEstado()                            { return estado; }
    public void       setEstado(String estado)              { this.estado = estado; }

    public String     getTipo()                              { return tipo; }
    public void       setTipo(String tipo)                  { this.tipo = tipo; }

    public String     getLinkSala()                          { return linkSala; }
    public void       setLinkSala(String linkSala)          { this.linkSala = linkSala; }

    public String     getConsultorio()                        { return consultorio; }
    public void       setConsultorio(String consultorio)     { this.consultorio = consultorio; }

    public String     getTipoReserva()                        { return tipoReserva; }
    public void       setTipoReserva(String tipoReserva)     { this.tipoReserva = tipoReserva; }

    public BigDecimal getMontoExtra()                         { return montoExtra; }
    public void       setMontoExtra(BigDecimal montoExtra)   { this.montoExtra = montoExtra; }

    public Integer    getIdPaciente()                         { return idPaciente; }
    public void       setIdPaciente(Integer idPaciente)      { this.idPaciente = idPaciente; }

    public Integer    getIdDoctor()                           { return idDoctor; }
    public void       setIdDoctor(Integer idDoctor)          { this.idDoctor = idDoctor; }
}
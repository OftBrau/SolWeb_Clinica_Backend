package edu.upn.clinica.backend.asistente.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class CitaPendiente {

    private Integer    idCita;
    private Integer    idPaciente;
    private String     nombrePaciente;
    private String     emailPaciente;
    private String     codigoEstudiante;
    private Integer    idEspecialidad;
    private String     nombreEspecialidad;
    private BigDecimal costoExtra;
    private LocalDate  fecha;
    private LocalTime  hora;
    private String     tipo;
    private String     motivo;
    private String     estado;
    private String     tipoReserva;

    public CitaPendiente() {}

    public Integer    getIdCita() { return idCita; }
    public void       setIdCita(Integer idCita) { this.idCita = idCita; }

    public Integer    getIdPaciente() { return idPaciente; }
    public void       setIdPaciente(Integer idPaciente) { this.idPaciente = idPaciente; }

    public String     getNombrePaciente() { return nombrePaciente; }
    public void       setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }

    public String     getEmailPaciente() { return emailPaciente; }
    public void       setEmailPaciente(String emailPaciente) { this.emailPaciente = emailPaciente; }

    public String     getCodigoEstudiante() { return codigoEstudiante; }
    public void       setCodigoEstudiante(String codigoEstudiante) { this.codigoEstudiante = codigoEstudiante; }

    public Integer    getIdEspecialidad() { return idEspecialidad; }
    public void       setIdEspecialidad(Integer idEspecialidad) { this.idEspecialidad = idEspecialidad; }

    public String     getNombreEspecialidad() { return nombreEspecialidad; }
    public void       setNombreEspecialidad(String nombreEspecialidad) { this.nombreEspecialidad = nombreEspecialidad; }

    public BigDecimal getCostoExtra() { return costoExtra; }
    public void       setCostoExtra(BigDecimal costoExtra) { this.costoExtra = costoExtra; }

    public LocalDate  getFecha() { return fecha; }
    public void       setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime  getHora() { return hora; }
    public void       setHora(LocalTime hora) { this.hora = hora; }

    public String     getTipo() { return tipo; }
    public void       setTipo(String tipo) { this.tipo = tipo; }

    public String     getMotivo() { return motivo; }
    public void       setMotivo(String motivo) { this.motivo = motivo; }

    public String     getEstado() { return estado; }
    public void       setEstado(String estado) { this.estado = estado; }

    public String     getTipoReserva() { return tipoReserva; }
    public void       setTipoReserva(String tipoReserva) { this.tipoReserva = tipoReserva; }
}

package edu.upn.clinica.backend.scheduling.dto;

import edu.upn.clinica.backend.scheduling.model.ScheduleAppointment;

import java.math.BigDecimal;

public class AppointmentDTO {
    private Integer id;
    private Integer doctorId;
    private String doctorName;
    private Integer patientId;
    private String patientName;
    private String date;
    private String startTime;
    private String endTime;
    private String status;
    private String notes;
    private String createdAt;
    private String tipoReserva;
    private BigDecimal montoExtra;
    private String tipo;
    private String motivo;
    private Integer idEspecialidad;

    public AppointmentDTO() {}

    public AppointmentDTO(ScheduleAppointment a) {
        this.id = a.getId();
        this.doctorId = a.getDoctorId();
        this.patientId = a.getPatientId();
        this.date = a.getDate().toString();
        this.startTime = a.getStartTime().toString();
        this.endTime = a.getEndTime() != null ? a.getEndTime().toString() : a.getStartTime().plusMinutes(30).toString();
        this.status = a.getStatus();
        this.notes = a.getNotes();
        this.createdAt = a.getCreatedAt() != null ? a.getCreatedAt().toString() : null;
        this.tipoReserva = a.getTipoReserva();
        this.montoExtra = a.getMontoExtra();
        this.tipo = a.getTipo();
        this.motivo = a.getMotivo();
        this.idEspecialidad = a.getIdEspecialidad();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public Integer getPatientId() { return patientId; }
    public void setPatientId(Integer patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getTipoReserva() { return tipoReserva; }
    public void setTipoReserva(String tipoReserva) { this.tipoReserva = tipoReserva; }

    public BigDecimal getMontoExtra() { return montoExtra; }
    public void setMontoExtra(BigDecimal montoExtra) { this.montoExtra = montoExtra; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Integer getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(Integer idEspecialidad) { this.idEspecialidad = idEspecialidad; }
}

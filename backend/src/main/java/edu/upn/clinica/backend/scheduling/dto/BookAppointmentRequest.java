package edu.upn.clinica.backend.scheduling.dto;

public class BookAppointmentRequest {
    private Integer doctorId;
    private String date;
    private String startTime;

    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
}

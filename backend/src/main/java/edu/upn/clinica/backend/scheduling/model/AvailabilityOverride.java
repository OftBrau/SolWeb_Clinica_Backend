package edu.upn.clinica.backend.scheduling.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "availability_override")
public class AvailabilityOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_override")
    private Integer id;

    @Column(name = "id_doctor", nullable = false)
    private Integer doctorId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "override_type", nullable = false, length = 10)
    private String overrideType;

    @Column(length = 255)
    private String reason;

    public AvailabilityOverride() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getDoctorId() { return doctorId; }
    public void setDoctorId(Integer doctorId) { this.doctorId = doctorId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getOverrideType() { return overrideType; }
    public void setOverrideType(String overrideType) { this.overrideType = overrideType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

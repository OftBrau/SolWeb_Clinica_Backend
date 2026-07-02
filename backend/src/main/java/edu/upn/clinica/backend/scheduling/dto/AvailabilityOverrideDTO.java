package edu.upn.clinica.backend.scheduling.dto;

import edu.upn.clinica.backend.scheduling.model.AvailabilityOverride;

public class AvailabilityOverrideDTO {
    private Integer id;
    private String date;
    private String startTime;
    private String endTime;
    private String overrideType;
    private String reason;

    public AvailabilityOverrideDTO() {}

    public AvailabilityOverrideDTO(AvailabilityOverride o) {
        this.id = o.getId();
        this.date = o.getDate().toString();
        this.startTime = o.getStartTime().toString();
        this.endTime = o.getEndTime().toString();
        this.overrideType = o.getOverrideType();
        this.reason = o.getReason();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getOverrideType() { return overrideType; }
    public void setOverrideType(String overrideType) { this.overrideType = overrideType; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

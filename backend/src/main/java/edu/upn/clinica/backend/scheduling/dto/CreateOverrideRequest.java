package edu.upn.clinica.backend.scheduling.dto;

public class CreateOverrideRequest {
    private String date;
    private String startTime;
    private String endTime;
    private String overrideType;
    private String reason;

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

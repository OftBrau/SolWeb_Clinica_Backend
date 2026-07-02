package edu.upn.clinica.backend.scheduling.dto;

import edu.upn.clinica.backend.scheduling.model.AvailabilityTemplate;

public class AvailabilityTemplateDTO {
    private Integer id;
    private String dayOfWeek;
    private String startTime;
    private String endTime;

    public AvailabilityTemplateDTO() {}

    public AvailabilityTemplateDTO(AvailabilityTemplate t) {
        this.id = t.getId();
        this.dayOfWeek = t.getDayOfWeek();
        this.startTime = t.getStartTime().toString();
        this.endTime = t.getEndTime().toString();
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}

package edu.upn.clinica.backend.scheduling.dto;

public class AvailableDateDTO {
    private String date;
    private int slotCount;

    public AvailableDateDTO() {}

    public AvailableDateDTO(String date, int slotCount) {
        this.date = date;
        this.slotCount = slotCount;
    }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getSlotCount() { return slotCount; }
    public void setSlotCount(int slotCount) { this.slotCount = slotCount; }
}

package edu.upn.clinica.backend.scheduling.dto;

public class DoctorSummaryDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String specialty;

    public DoctorSummaryDTO() {}

    public DoctorSummaryDTO(Integer id, String firstName, String lastName, String specialty) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
}

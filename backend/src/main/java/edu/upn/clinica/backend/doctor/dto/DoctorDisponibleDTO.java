package edu.upn.clinica.backend.doctor.dto;

// ============================================================
//  DoctorDisponibleDTO.java
//  Respuesta del GET /api/cita-publica/doctores/{especialidad}
//  Solo expone lo necesario para el formulario público
// ============================================================
public class DoctorDisponibleDTO {

    private Integer idDoctor;
    private String  nombre;       // nombre completo: "Dra. María Torres"
    private String  especialidad;

    public DoctorDisponibleDTO() {}

    public DoctorDisponibleDTO(Integer idDoctor, String nombre, String especialidad) {
        this.idDoctor     = idDoctor;
        this.nombre       = nombre;
        this.especialidad = especialidad;
    }

    public Integer getIdDoctor()                            { return idDoctor; }
    public void    setIdDoctor(Integer idDoctor)           { this.idDoctor = idDoctor; }

    public String  getNombre()                              { return nombre; }
    public void    setNombre(String nombre)                { this.nombre = nombre; }

    public String  getEspecialidad()                        { return especialidad; }
    public void    setEspecialidad(String especialidad)    { this.especialidad = especialidad; }
}
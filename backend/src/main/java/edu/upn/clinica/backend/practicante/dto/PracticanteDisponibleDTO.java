package edu.upn.clinica.backend.practicante.dto;

public class PracticanteDisponibleDTO {
    private Integer idPracticante;
    private String nombre;
    private String apellido;
    private String email;

    public PracticanteDisponibleDTO() {}

    public Integer getIdPracticante() { return idPracticante; }
    public void setIdPracticante(Integer v) { this.idPracticante = v; }

    public String getNombre() { return nombre; }
    public void setNombre(String v) { this.nombre = v; }

    public String getApellido() { return apellido; }
    public void setApellido(String v) { this.apellido = v; }

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
}

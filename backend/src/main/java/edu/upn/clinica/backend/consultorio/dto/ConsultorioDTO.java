package edu.upn.clinica.backend.consultorio.dto;

public class ConsultorioDTO {
    private Integer idConsultorio;
    private String nombre;
    private String ubicacion;
    private String estado;

    public ConsultorioDTO() {}

    public ConsultorioDTO(edu.upn.clinica.backend.consultorio.model.Consultorio c) {
        this.idConsultorio = c.getIdConsultorio();
        this.nombre = c.getNombre();
        this.ubicacion = c.getUbicacion();
        this.estado = c.getEstado();
    }

    public Integer getIdConsultorio() { return idConsultorio; }
    public void setIdConsultorio(Integer id) { this.idConsultorio = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

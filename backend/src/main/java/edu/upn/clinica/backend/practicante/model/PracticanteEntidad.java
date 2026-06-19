package edu.upn.clinica.backend.practicante.model;

public class PracticanteEntidad {
    private Integer idAsignacion;
    private Integer idPracticante;
    private Integer idSupervisor;

    private String nombreUsuario;
    private String apellidoUsuario;
    private String emailUsuario;
    private String nombreDoctor;
    private String apellidoDoctor;

    public PracticanteEntidad() {}

    public Integer getIdAsignacion() { return idAsignacion; }
    public void setIdAsignacion(Integer v) { this.idAsignacion = v; }

    public Integer getIdPracticante() { return idPracticante; }
    public void setIdPracticante(Integer v) { this.idPracticante = v; }

    public Integer getIdSupervisor() { return idSupervisor; }
    public void setIdSupervisor(Integer v) { this.idSupervisor = v; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String v) { this.nombreUsuario = v; }

    public String getApellidoUsuario() { return apellidoUsuario; }
    public void setApellidoUsuario(String v) { this.apellidoUsuario = v; }

    public String getEmailUsuario() { return emailUsuario; }
    public void setEmailUsuario(String v) { this.emailUsuario = v; }

    public String getNombreDoctor() { return nombreDoctor; }
    public void setNombreDoctor(String v) { this.nombreDoctor = v; }

    public String getApellidoDoctor() { return apellidoDoctor; }
    public void setApellidoDoctor(String v) { this.apellidoDoctor = v; }
}

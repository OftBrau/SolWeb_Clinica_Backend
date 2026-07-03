package edu.upn.clinica.backend.perfil.model;

import java.time.LocalDate;

public class PerfilProfesional {
    private Integer idPerfil;
    private Integer idPracticante;
    private String tituloProfesional;
    private String universidad;
    private Integer anioGraduacion;
    private String biografia;
    private String linkedinUrl;
    private Boolean activo;

    // datos del usuario
    private String nombreCompleto;
    private String email;
    private String fotoUrl;
    private String bannerUrl;
    private String cvUrl;

    public PerfilProfesional() {}

    public Integer getIdPerfil() { return idPerfil; }
    public void setIdPerfil(Integer idPerfil) { this.idPerfil = idPerfil; }
    public Integer getIdPracticante() { return idPracticante; }
    public void setIdPracticante(Integer idPracticante) { this.idPracticante = idPracticante; }
    public String getTituloProfesional() { return tituloProfesional; }
    public void setTituloProfesional(String tituloProfesional) { this.tituloProfesional = tituloProfesional; }
    public String getUniversidad() { return universidad; }
    public void setUniversidad(String universidad) { this.universidad = universidad; }
    public Integer getAnioGraduacion() { return anioGraduacion; }
    public void setAnioGraduacion(Integer anioGraduacion) { this.anioGraduacion = anioGraduacion; }
    public String getBiografia() { return biografia; }
    public void setBiografia(String biografia) { this.biografia = biografia; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }
    public String getCvUrl() { return cvUrl; }
    public void setCvUrl(String cvUrl) { this.cvUrl = cvUrl; }
}

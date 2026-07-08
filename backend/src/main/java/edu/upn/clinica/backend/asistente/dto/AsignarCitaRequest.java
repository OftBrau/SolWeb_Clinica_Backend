package edu.upn.clinica.backend.asistente.dto;

import jakarta.validation.constraints.NotNull;

public class AsignarCitaRequest {

    @NotNull
    private Integer idDoctor;

    private Integer idConsultorio;

    public AsignarCitaRequest() {}

    public Integer getIdDoctor() { return idDoctor; }
    public void    setIdDoctor(Integer idDoctor) { this.idDoctor = idDoctor; }

    public Integer getIdConsultorio() { return idConsultorio; }
    public void    setIdConsultorio(Integer idConsultorio) { this.idConsultorio = idConsultorio; }
}

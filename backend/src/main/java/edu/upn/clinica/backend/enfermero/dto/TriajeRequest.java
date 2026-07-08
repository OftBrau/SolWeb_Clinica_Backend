package edu.upn.clinica.backend.enfermero.dto;

import jakarta.validation.constraints.NotNull;

public class TriajeRequest {

    @NotNull
    private Integer idCita;

    private String  presionArterial;
    private Double  temperatura;
    private Integer frecuenciaCardiaca;
    private Double  saturacion;
    private Double  peso;
    private Double  talla;
    private String  motivoConsulta;
    private String  notas;

    public TriajeRequest() {}

    public Integer getIdCita() { return idCita; }
    public void    setIdCita(Integer idCita) { this.idCita = idCita; }

    public String  getPresionArterial() { return presionArterial; }
    public void    setPresionArterial(String presionArterial) { this.presionArterial = presionArterial; }

    public Double  getTemperatura() { return temperatura; }
    public void    setTemperatura(Double temperatura) { this.temperatura = temperatura; }

    public Integer getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public void    setFrecuenciaCardiaca(Integer frecuenciaCardiaca) { this.frecuenciaCardiaca = frecuenciaCardiaca; }

    public Double  getSaturacion() { return saturacion; }
    public void    setSaturacion(Double saturacion) { this.saturacion = saturacion; }

    public Double  getPeso() { return peso; }
    public void    setPeso(Double peso) { this.peso = peso; }

    public Double  getTalla() { return talla; }
    public void    setTalla(Double talla) { this.talla = talla; }

    public String  getMotivoConsulta() { return motivoConsulta; }
    public void    setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }

    public String  getNotas() { return notas; }
    public void    setNotas(String notas) { this.notas = notas; }
}

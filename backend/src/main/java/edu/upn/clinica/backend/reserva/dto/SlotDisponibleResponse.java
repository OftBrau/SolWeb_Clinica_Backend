package edu.upn.clinica.backend.reserva.dto;

import java.time.LocalTime;

public class SlotDisponibleResponse {

    private LocalTime hora;
    private boolean   disponible;
    private Integer   idDoctor;
    private String    nombreDoctor;

    public SlotDisponibleResponse() {}

    public SlotDisponibleResponse(LocalTime hora, boolean disponible) {
        this.hora = hora;
        this.disponible = disponible;
    }

    public LocalTime getHora() { return hora; }
    public void      setHora(LocalTime hora) { this.hora = hora; }

    public boolean   isDisponible() { return disponible; }
    public void      setDisponible(boolean disponible) { this.disponible = disponible; }

    public Integer   getIdDoctor() { return idDoctor; }
    public void      setIdDoctor(Integer idDoctor) { this.idDoctor = idDoctor; }

    public String    getNombreDoctor() { return nombreDoctor; }
    public void      setNombreDoctor(String nombreDoctor) { this.nombreDoctor = nombreDoctor; }
}

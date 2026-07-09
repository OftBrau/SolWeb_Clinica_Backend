package edu.upn.clinica.backend.farmacia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CheckoutPublicRequest {
    @NotBlank private String nombre;
    @Email private String email;
    private String telefono;

    private String codigoSunat;

    @NotNull
    private List<ItemCheckout> items;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCodigoSunat() { return codigoSunat; }
    public void setCodigoSunat(String codigoSunat) { this.codigoSunat = codigoSunat; }
    public List<ItemCheckout> getItems() { return items; }
    public void setItems(List<ItemCheckout> items) { this.items = items; }

    public static class ItemCheckout {
        private Integer idMedicamento;
        private Integer cantidad;

        public Integer getIdMedicamento() { return idMedicamento; }
        public void setIdMedicamento(Integer id) { this.idMedicamento = id; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer c) { this.cantidad = c; }
    }
}

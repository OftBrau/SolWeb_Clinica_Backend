package edu.upn.clinica.backend.farmacia.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public class CrearVentaRequest {

    private String codigoSunat;

    @NotNull
    private List<ItemVenta> items;

    public String getCodigoSunat() { return codigoSunat; }
    public void setCodigoSunat(String codigoSunat) { this.codigoSunat = codigoSunat; }

    public List<ItemVenta> getItems() { return items; }
    public void setItems(List<ItemVenta> items) { this.items = items; }

    public static class ItemVenta {
        @NotNull
        private Integer idMedicamento;
        @NotNull @Positive
        private Integer cantidad;

        public Integer getIdMedicamento() { return idMedicamento; }
        public void setIdMedicamento(Integer idMedicamento) { this.idMedicamento = idMedicamento; }

        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}

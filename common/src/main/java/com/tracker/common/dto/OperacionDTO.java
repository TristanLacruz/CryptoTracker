package com.tracker.common.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OperacionDTO {
    private String cryptoId;
    private double cantidad;
    private double precio;
    private String tipoOperacion;

    public OperacionDTO() {
        // Constructor por defecto obligatorio para deserialización
    }

    @JsonCreator
    public OperacionDTO(@JsonProperty("cryptoId") String cryptoId,
                        @JsonProperty("cantidad") double cantidad,
                        @JsonProperty("precio") double precio,
                        @JsonProperty("tipoOperacion") String tipoOperacion) {
        this.cryptoId = cryptoId;
        this.cantidad = cantidad;
        this.precio = precio;
        this.tipoOperacion = tipoOperacion;
    }

    // Getters y Setters
    public String getCryptoId() {
        return cryptoId;
    }

    public void setCryptoId(String cryptoId) {
        this.cryptoId = cryptoId;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }
}

package com.tracker.common.dto;

/**
 * Clase DTO para representar una solicitud de compra de criptomonedas.
 * Contiene información sobre el usuario, la criptomoneda, la cantidad y el precio.
 */
public class CriptoPosesionDTO {
    private String simbolo;
    private double cantidad;
    private double valorTotal; 

    public CriptoPosesionDTO() {
	}
    
    public CriptoPosesionDTO(String simbolo, double cantidad, double valorTotal) {
        this.simbolo = simbolo;
        this.cantidad = cantidad;
        this.valorTotal = valorTotal;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }
}

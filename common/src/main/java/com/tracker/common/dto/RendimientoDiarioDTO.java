package com.tracker.common.dto;

/**
 * Clase DTO para representar el rendimiento diario de una criptomoneda.
 * Contiene información sobre el día y la ganancia obtenida.
 */
public class RendimientoDiarioDTO {
    private int dia;
    private double ganancia;

    public RendimientoDiarioDTO(int dia, double ganancia) {
        this.dia = dia;
        this.ganancia = ganancia;
    }

    public int getDia() {
        return dia;
    }

    public double getGanancia() {
        return ganancia;
    }
}

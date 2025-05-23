package com.tracker.common.dto;

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

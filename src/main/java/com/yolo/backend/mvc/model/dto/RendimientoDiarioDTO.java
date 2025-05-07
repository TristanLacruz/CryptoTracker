package com.yolo.backend.mvc.model.dto;

public class RendimientoDiarioDTO {
    private int dia;
    private double ganancia; // puede ser negativa

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

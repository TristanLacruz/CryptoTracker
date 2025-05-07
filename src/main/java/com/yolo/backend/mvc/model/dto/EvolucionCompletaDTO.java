package com.yolo.backend.mvc.model.dto;

public class EvolucionCompletaDTO {
    private int dia;
    private double valor;
    private double ganancia;

    public EvolucionCompletaDTO(int dia, double valor, double ganancia) {
        this.dia = dia;
        this.valor = valor;
        this.ganancia = ganancia;
    }

    public int getDia() {
        return dia;
    }

    public double getValor() {
        return valor;
    }

    public double getGanancia() {
        return ganancia;
    }
}

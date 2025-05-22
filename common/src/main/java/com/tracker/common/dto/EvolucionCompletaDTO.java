package com.tracker.common.dto;

public class EvolucionCompletaDTO {

    private int dia;
    private double valorTotal;
    private double ganancia;

    public EvolucionCompletaDTO() {}

    public EvolucionCompletaDTO(int dia, double valorTotal, double ganancia) {
        this.dia = dia;
        this.valorTotal = valorTotal;
        this.ganancia = ganancia;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public double getGanancia() {
        return ganancia;
    }

    public void setGanancia(double ganancia) {
        this.ganancia = ganancia;
    }
}

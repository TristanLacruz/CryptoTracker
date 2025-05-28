package com.tracker.common.dto;

public class EvolucionCompletaDTO {

    private int dia;
    private double valorTotal;
    private double saldoEuros;
    private double valorCriptos;

    public EvolucionCompletaDTO() {
    }

    public EvolucionCompletaDTO(int dia, double valorTotal, double saldoEuros, double valorCriptos) {
        this.dia = dia;
        this.valorTotal = valorTotal;
        this.saldoEuros = saldoEuros;
        this.valorCriptos = valorCriptos;
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

    public double getSaldoEuros() {
        return saldoEuros;
    }

    public void setSaldoEuros(double saldoEuros) {
        this.saldoEuros = saldoEuros;
    }
}

package com.yolo.backend.mvc.model.dto;

public class ValorDiarioDTO {
    private int dia;
    private double valor;

    public ValorDiarioDTO(int dia, double valor) {
        this.dia = dia;
        this.valor = valor;
    }

    public int getDia() {
        return dia;
    }

    public double getValor() {
        return valor;
    }
}

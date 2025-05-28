package com.tracker.common.dto;

import java.time.LocalDate;

public class EstadoPortafolioDTO {
    private LocalDate fecha;
    private double saldoDisponible;
    private double valorCriptos;
    private double total;

    public EstadoPortafolioDTO(LocalDate fecha, double saldoDisponible, double valorCriptos) {
        this.fecha = fecha;
        this.saldoDisponible = saldoDisponible;
        this.valorCriptos = valorCriptos;
        this.total = saldoDisponible + valorCriptos;
    }

    // Getters y setters
}

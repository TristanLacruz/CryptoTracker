package com.tracker.common.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class ValorDiarioDTO {
    private int dia;
    private double valor;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate fecha;
    private Double saldoTotal;

    public ValorDiarioDTO() {
    }

    public ValorDiarioDTO(LocalDate fecha, Double saldoTotal) {
        this.fecha = fecha;
        this.saldoTotal = saldoTotal;
    }

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
    
    public LocalDate getFecha() {
		return fecha;
	}
    
    public Double getSaldoTotal() {
		return saldoTotal;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}
	
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public void setSaldoTotal(Double saldoTotal) {
		this.saldoTotal = saldoTotal;
	}
}

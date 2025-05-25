package com.tracker.backend.mvc.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "criptomonedas")
public class Criptomoneda {

    @Id
    private String id;
    private String simbolo;
    private String nombre;
    private double precioActual;
    private LocalDateTime ultimaActualizacion;

    public Criptomoneda() {
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public Criptomoneda(String id) {
    	this.id = id;
    }
    
    public Criptomoneda(String simbolo, String nombre, double precioActual) {
        this.simbolo = simbolo;
        this.nombre = nombre;
        this.precioActual = precioActual;
        this.ultimaActualizacion = LocalDateTime.now();
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSimbolo() {
		return simbolo;
	}

	public void setSimbolo(String simbolo) {
		this.simbolo = simbolo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getPrecioActual() {
		return precioActual;
	}

	public void setPrecioActual(double precioActual) {
		this.precioActual = precioActual;
	}

	public LocalDateTime getUltimaActualizacion() {
		return ultimaActualizacion;
	}

	public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
		this.ultimaActualizacion = ultimaActualizacion;
	}
}

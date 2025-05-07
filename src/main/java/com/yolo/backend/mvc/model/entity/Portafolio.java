package com.yolo.backend.mvc.model.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "portafolios")
public class Portafolio {

	@Id
	private String id;

	private String usuarioId;
    private Map<String, Double> criptomonedas = new HashMap<>(); // key: símbolo, value: cantidad
	private double saldo; // 💰 Dinero ficticio disponible

	public Portafolio() {
	}
	
	@Data
    public static class CriptoActivo {
        private String cryptoId;
        private String nombre;
        private double cantidad; // cantidad que posee
    }

	public Portafolio(String id) {
		this.id = id;
	}

	public Portafolio(String usuarioId, String idCriptomoneda, double cantidadAlmacenada, double precioMedioCompra) {
		this.usuarioId = usuarioId;
	}

	public String getId() {
		return id;
	}
	
	public double getSaldo() {
	    return saldo;
	}

	public void setSaldo(double saldo) {
	    this.saldo = saldo;
	}


	public void setId(String id) {
		this.id = id;
	}

	public String getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Map<String, Double> getCriptomonedas() {
		return criptomonedas;
	}

	public void setCriptomonedas(Map<String, Double> criptomonedas) {
		this.criptomonedas = criptomonedas;
	}

	
	public void agregarCripto(String simbolo, double cantidad) {
	    this.criptomonedas.merge(simbolo, cantidad, Double::sum);
	}

	public void actualizarConCompra(String simbolo, double cantidad, double coste) {
	    // Restamos el dinero gastado
	    this.saldo -= coste;

	    // Sumamos la cantidad comprada al mapa de criptomonedas
	    this.criptomonedas.merge(simbolo, cantidad, Double::sum);
	}

	
}

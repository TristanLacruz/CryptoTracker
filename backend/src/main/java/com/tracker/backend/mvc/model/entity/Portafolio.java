package com.tracker.backend.mvc.model.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "portafolios")
public class Portafolio {

    @Id
    private String id;
    private String usuarioId;
    private Map<String, Double> criptomonedas = new HashMap<>();
    private double saldo;

    public Portafolio() {}

    public Portafolio(String id) {
        this.id = id;
    }

	public Portafolio(String usuarioId, String idCriptomoneda, double cantidadAlmacenada, double precioMedioCompra) {
		this.usuarioId = usuarioId;
	}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public Map<String, Double> getCriptomonedas() {
        return criptomonedas;
    }
    public void setCriptomonedas(Map<String, Double> criptomonedas) {
        this.criptomonedas = criptomonedas;
    }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public void agregarCripto(String simbolo, double cantidad) {
        if (criptomonedas == null) {
            criptomonedas = new HashMap<>();
        }
        criptomonedas.put(simbolo, criptomonedas.getOrDefault(simbolo, 0.0) + cantidad);
    }
    
    public void restarCripto(String simbolo, double cantidad) {
        if (this.criptomonedas.containsKey(simbolo)) {
            double nuevaCantidad = this.criptomonedas.get(simbolo) - cantidad;
            if (nuevaCantidad <= 0) {
                this.criptomonedas.remove(simbolo);
            } else {
                this.criptomonedas.put(simbolo, nuevaCantidad);
            }
        }
    }

    public static class CriptoActivo {
        private String cryptoId;
        private String nombre;
        private double cantidad;

        public CriptoActivo() {}

        public String getCryptoId() { return cryptoId; }
        public void setCryptoId(String cryptoId) {
            this.cryptoId = cryptoId;
        }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public double getCantidad() { return cantidad; }
        public void setCantidad(double cantidad) {
            this.cantidad = cantidad;
        }
    }
}

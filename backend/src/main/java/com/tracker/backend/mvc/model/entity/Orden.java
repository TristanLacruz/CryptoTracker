package com.tracker.backend.mvc.model.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Ordenes")
public class Orden {

	@Id
    private String id;

    private String usuarioId;
    private String tipo; // "Mercado", "Limite"
    private String cryptoId;
    private double cantidad;
    private double objetivoPrecio;
    private String estado; // "activa", "ejecutada", "cancelada"
    private LocalDateTime creadoEl;
    private LocalDateTime ejecutadoEl;
    
    public Orden() {
    	this.creadoEl = LocalDateTime.now();
		this.estado = "activa";
    }
    
    public Orden(String id) {
		this.id = id;
	}
    
	public Orden(String usuarioId, String tipo, String cryptoId, double cantidad, double objetivoPrecio) {
		this.usuarioId = usuarioId;
		this.tipo = tipo;
		this.cryptoId = cryptoId;
		this.cantidad = cantidad;
		this.objetivoPrecio = objetivoPrecio;
		this.creadoEl = LocalDateTime.now();
		this.estado = "activa";
	}
	
	public String getId() {
		return id;
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
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getCryptoId() {
		return cryptoId;
	}
	public void setCryptoId(String cryptoId) {
		this.cryptoId = cryptoId;
	}
	public double getCantidad() {
		return cantidad;
	}
	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}
	public double getObjetivoPrecio() {
		return objetivoPrecio;
	}
	public void setObjetivoPrecio(double objetivoPrecio) {
		this.objetivoPrecio = objetivoPrecio;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public LocalDateTime getCreadoEl() {
		return creadoEl;
	}
	public void setCreadoEl(LocalDateTime creadoEl) {
		this.creadoEl = creadoEl;
	}
	public LocalDateTime getEjecutadoEl() {
		return ejecutadoEl;
	}
	public void setEjecutadoEl(LocalDateTime ejecutadoEl) {
		this.ejecutadoEl = ejecutadoEl;
	}
    
    
}

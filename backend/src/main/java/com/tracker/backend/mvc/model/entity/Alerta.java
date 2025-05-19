package com.tracker.backend.mvc.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "Alertas")
public class Alerta {

    @Id
    private String id;
    private String usuarioId;
    private String cryptoId;
    private double objetivoPrecio;
    private boolean ejecutado; 
    private LocalDateTime creadoEl;

    public Alerta() {
        this.creadoEl = LocalDateTime.now();
        this.ejecutado = false;
    }

    public Alerta(String id) {
    	this.id = id;
    }
    
    public Alerta(String usuarioId, String cryptoId, double objetivoPrecio) {
        this.usuarioId = usuarioId;
        this.cryptoId = cryptoId;
        this.objetivoPrecio = objetivoPrecio;
        this.ejecutado = false;
        this.creadoEl = LocalDateTime.now();
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

	public String getCryptoId() {
		return cryptoId;
	}

	public void setCryptoId(String cryptoId) {
		this.cryptoId = cryptoId;
	}

	public double getObjetivoPrecio() {
		return objetivoPrecio;
	}

	public void setObjetivoPrecio(double objetivoPrecio) {
		this.objetivoPrecio = objetivoPrecio;
	}

	public boolean getEjecutado() {
		return ejecutado;
	}

	public void setEjecutado(boolean ejecutado) {
		this.ejecutado = ejecutado;
	}

	public LocalDateTime getCreadoEl() {
		return creadoEl;
	}

	public void setCreadoEl(LocalDateTime creadoEl) {
		this.creadoEl = creadoEl;
	}
    
    
   
}

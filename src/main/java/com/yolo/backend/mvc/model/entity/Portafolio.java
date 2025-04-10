package com.yolo.backend.mvc.model.entity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "portafolios")
public class Portafolio {

	@Id
	private String id;

	private String usuarioId;
	private Map<String, Double> criptomonedas = new HashMap<>(); // key: simbolo, value: cantidad

	public Portafolio() {
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

}

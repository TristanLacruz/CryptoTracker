package com.tracker.common.dto;

public class VentaRequestDTO {

	private String usuarioId;
	private String simbolo;
	private String nombreCrypto;
	private double cantidadCrypto;
	private double precio;

	public VentaRequestDTO() {}

	public String getUsuarioId() {
		return usuarioId;
	}
	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getSimbolo() {
		return simbolo;
	}
	public void setSimbolo(String simbolo) {
		this.simbolo = simbolo;
	}

	public String getNombreCrypto() {
		return nombreCrypto;
	}
	public void setNombreCrypto(String nombreCrypto) {
		this.nombreCrypto = nombreCrypto;
	}

	public double getCantidadCrypto() {
		return cantidadCrypto;
	}
	public void setCantidadCrypto(double cantidadCrypto) {
		this.cantidadCrypto = cantidadCrypto;
	}

	public double getPrecio() {
		return precio;
	}
	public void setPrecio(double precio) {
		this.precio = precio;
	}
}

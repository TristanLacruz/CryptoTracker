package com.tracker.backend.mvc.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

/**
 * Clase que representa una transacción de compra o venta de criptomonedas.
 * Contiene información sobre el usuario, la criptomoneda, el tipo de transacción,
 * la cantidad de criptomonedas, el precio de la transacción, el valor total y la fecha de la transacción.
 */
@Document(collection = "transacciones")
public class Transaccion {

    @Id
    private String id;
    
    private String usuarioId;
    private String cryptoId;
    private TransactionType tipoTransaccion; // "COMPRAR" o "VENDER"
    private double cantidadCrypto;
    private double precioTransaccion;
    private double valorTotal;
    private LocalDateTime fechaTransaccion;

    public Transaccion() {
        this.fechaTransaccion = LocalDateTime.now();
    }

    public Transaccion(String id) {
    	this.id = id;
    }
    
    public Transaccion(String usuarioId, String cryptoId, TransactionType tipoTransaccion, double cantidadCrypto, double precioTransaccion) {
        this.usuarioId = usuarioId;
        this.cryptoId = cryptoId;
        this.tipoTransaccion = tipoTransaccion;
        this.cantidadCrypto = cantidadCrypto;
        this.precioTransaccion = precioTransaccion;
        this.valorTotal = cantidadCrypto * precioTransaccion;
        this.fechaTransaccion = LocalDateTime.now();
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

	public TransactionType getTipoTransaccion() {
		return tipoTransaccion;
	}

	public void setTipoTransaccion(TransactionType tipoTransaccion) {
		this.tipoTransaccion = tipoTransaccion;
	}

	public double getCantidadCrypto() {
		return cantidadCrypto;
	}

	public void setCantidadCrypto(double cantidadCrypto) {
		this.cantidadCrypto = cantidadCrypto;
	}

	public double getPrecioTransaccion() {
		return precioTransaccion;
	}

	public void setPrecioTransaccion(double precioTransaccion) {
		this.precioTransaccion = precioTransaccion;
	}

	public double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public LocalDateTime getFechaTransaccion() {
		return fechaTransaccion;
	}

	public void setFechaTransaccion(LocalDateTime fechaTransaccion) {
		this.fechaTransaccion = fechaTransaccion;
	}
}

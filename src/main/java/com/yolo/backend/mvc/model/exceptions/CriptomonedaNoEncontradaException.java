package com.yolo.backend.mvc.model.exceptions;

public class CriptomonedaNoEncontradaException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public CriptomonedaNoEncontradaException() {
		super();
	}
	
	public CriptomonedaNoEncontradaException(String id) {
		super("Criptomoneda " + id + " no encontrada");
	}
}

package com.yolo.backend.mvc.model.exceptions;

public class TransaccionNoEncontradaException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public TransaccionNoEncontradaException() {
		super();
	}
	
	public TransaccionNoEncontradaException(String id) {
		super("Transaccion " + id + " no encontrada");
	}
}

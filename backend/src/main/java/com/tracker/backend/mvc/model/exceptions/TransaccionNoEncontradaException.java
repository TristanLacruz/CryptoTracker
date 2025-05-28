package com.tracker.backend.mvc.model.exceptions;

/**
 * Excepción personalizada que se lanza cuando no se encuentra una transacción.
 * Extiende de RuntimeException para indicar un error en tiempo de ejecución.
 */
public class TransaccionNoEncontradaException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public TransaccionNoEncontradaException() {
		super();
	}
	
	public TransaccionNoEncontradaException(String id) {
		super("Transaccion " + id + " no encontrada");
	}
}

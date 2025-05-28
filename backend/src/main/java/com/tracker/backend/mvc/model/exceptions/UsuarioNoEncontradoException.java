package com.tracker.backend.mvc.model.exceptions;

/**
 * Excepción personalizada que se lanza cuando no se encuentra una criptomoneda.
 * Extiende de RuntimeException para indicar un error en tiempo de ejecución.
 */
public class UsuarioNoEncontradoException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public UsuarioNoEncontradoException() {
		super();
	}
	
	public UsuarioNoEncontradoException(String id) {
		super("Usuario " + id + " no encontrado");
	}
}

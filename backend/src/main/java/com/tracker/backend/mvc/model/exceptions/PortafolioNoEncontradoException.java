package com.tracker.backend.mvc.model.exceptions;

/**
 * Excepción personalizada que se lanza cuando no se encuentra una portafolio.
 * Extiende de RuntimeException para indicar un error en tiempo de ejecución.
 */
public class PortafolioNoEncontradoException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public PortafolioNoEncontradoException() {
		super();
	}
	
	public PortafolioNoEncontradoException(String id) {
		super("Portafolio " + id + " no encontrado");
	}
}

package com.tracker.backend.mvc.model.exceptions;

public class PortafolioNoEncontradoException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public PortafolioNoEncontradoException() {
		super();
	}
	
	public PortafolioNoEncontradoException(String id) {
		super("Portafolio " + id + " no encontrado");
	}
}

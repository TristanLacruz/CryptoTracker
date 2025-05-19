package com.tracker.backend.mvc.model.exceptions;

public class OrdenNoEncontradaException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public OrdenNoEncontradaException() {
		super();
	}
	
	public OrdenNoEncontradaException(String id) {
		super("Orden " + id + " no encontrada");
	}
}

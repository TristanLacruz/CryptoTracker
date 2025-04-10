package com.yolo.backend.mvc.model.exceptions;

public class AlertaNoEncontradaException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public AlertaNoEncontradaException() {
		super();
	}
	
	public AlertaNoEncontradaException(String id) {
		super("Alerta " + id + " no encontrada");
	}
}

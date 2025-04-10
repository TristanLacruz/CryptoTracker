package com.yolo.backend.mvc.model.exceptions;

public class UsuarioNoEncontradoException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public UsuarioNoEncontradoException() {
		super();
	}
	
	public UsuarioNoEncontradoException(String id) {
		super("Usuario " + id + " no encontrado");
	}
}

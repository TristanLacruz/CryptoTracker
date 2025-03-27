package com.yolo.backend.mvc.model.exceptions;

public class CryptocurrencyNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public CryptocurrencyNotFoundException() {
		super();
	}
	
	public CryptocurrencyNotFoundException(String id) {
		super("Cryptocurrency " + id + " not found");
	}
}

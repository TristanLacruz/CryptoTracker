package com.yolo.backend.mvc.model.exceptions;

public class TransactionNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public TransactionNotFoundException() {
		super();
	}
	
	public TransactionNotFoundException(String id) {
		super("User " + id + " not found");
	}
}

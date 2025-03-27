package com.yolo.backend.mvc.model.exceptions;

public class UserNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public UserNotFoundException() {
		super();
	}
	
	public UserNotFoundException(String id) {
		super("User " + id + " not found");
	}
}

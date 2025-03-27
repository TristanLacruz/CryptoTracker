package com.yolo.backend.mvc.model.exceptions;

public class AlertNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public AlertNotFoundException() {
		super();
	}
	
	public AlertNotFoundException(String id) {
		super("Alert " + id + " not found");
	}
}

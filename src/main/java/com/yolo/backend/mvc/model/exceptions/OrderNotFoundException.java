package com.yolo.backend.mvc.model.exceptions;

public class OrderNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public OrderNotFoundException() {
		super();
	}
	
	public OrderNotFoundException(String id) {
		super("NotificationLog " + id + " not found");
	}
}

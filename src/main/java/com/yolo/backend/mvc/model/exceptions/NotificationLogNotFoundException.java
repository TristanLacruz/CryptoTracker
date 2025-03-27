package com.yolo.backend.mvc.model.exceptions;

public class NotificationLogNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public NotificationLogNotFoundException() {
		super();
	}
	
	public NotificationLogNotFoundException(String id) {
		super("NotificationLog " + id + " not found");
	}
}

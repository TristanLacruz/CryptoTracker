package com.yolo.backend.mvc.model.exceptions;

public class PortfolioNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public PortfolioNotFoundException() {
		super();
	}
	
	public PortfolioNotFoundException(String id) {
		super("User " + id + " not found");
	}
}

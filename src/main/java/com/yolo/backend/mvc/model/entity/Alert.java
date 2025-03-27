package com.yolo.backend.mvc.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "alerts")
public class Alert {

    @Id
    private String id;
    private String userId;
    private String cryptoId;
    private double targetPrice;
    private boolean isTriggered;
    private LocalDateTime createdAt;

    public Alert() {
        this.createdAt = LocalDateTime.now();
        this.isTriggered = false;
    }

    public Alert(String id) {
    	this.id = id;
    }
    
    public Alert(String userId, String cryptoId, double targetPrice) {
        this.userId = userId;
        this.cryptoId = cryptoId;
        this.targetPrice = targetPrice;
        this.isTriggered = false;
        this.createdAt = LocalDateTime.now();
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCryptoId() {
		return cryptoId;
	}

	public void setCryptoId(String cryptoId) {
		this.cryptoId = cryptoId;
	}

	public double getTargetPrice() {
		return targetPrice;
	}

	public void setTargetPrice(double targetPrice) {
		this.targetPrice = targetPrice;
	}

	public boolean isTriggered() {
		return isTriggered;
	}

	public void setTriggered(boolean isTriggered) {
		this.isTriggered = isTriggered;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
   
}

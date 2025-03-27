package com.yolo.backend.mvc.model.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "orders")
public class Order {

	@Id
    private String id;

    private String userId;
    private String type; // "MARKET" o "LIMIT"
    private String cryptoId;
    private double amount;
    private double targetPrice;
    private String status; // "active", "executed", "cancelled"
    private LocalDateTime createdAt;
    private LocalDateTime executedAt;
    
    public Order() {
    	this.createdAt = LocalDateTime.now();
		this.status = "active";
    }
    
    public Order(String id) {
		this.id = id;
	}
    
	public Order(String userId, String type, String cryptoId, double amount, double targetPrice) {
		this.userId = userId;
		this.type = type;
		this.cryptoId = cryptoId;
		this.amount = amount;
		this.targetPrice = targetPrice;
		this.createdAt = LocalDateTime.now();
		this.status = "active";
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCryptoId() {
		return cryptoId;
	}
	public void setCryptoId(String cryptoId) {
		this.cryptoId = cryptoId;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getTargetPrice() {
		return targetPrice;
	}
	public void setTargetPrice(double targetPrice) {
		this.targetPrice = targetPrice;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getExecutedAt() {
		return executedAt;
	}
	public void setExecutedAt(LocalDateTime executedAt) {
		this.executedAt = executedAt;
	}
    
    
}

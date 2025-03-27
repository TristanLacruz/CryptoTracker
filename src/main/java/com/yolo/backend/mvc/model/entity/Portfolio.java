package com.yolo.backend.mvc.model.entity;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "portfolios")
public class Portfolio {

	@Id
	private String id;

	private String userId;
	private Map<String, Double> cryptos = new HashMap<>(); // key: symbol, value: cantidad

	public Portfolio() {
	}

	public Portfolio(String id) {
		this.id = id;
	}

	public Portfolio(String userId, String cryptoId, double amountHeld, double averageBuyPrice) {
		this.userId = userId;
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

	public Map<String, Double> getCryptos() {
		return cryptos;
	}

	public void setCryptos(Map<String, Double> cryptos) {
		this.cryptos = cryptos;
	}

}

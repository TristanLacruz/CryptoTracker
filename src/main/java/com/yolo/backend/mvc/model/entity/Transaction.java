package com.yolo.backend.mvc.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;
    
    private String userId;
    private String cryptoId;
    private String transactionType; // "BUY" or "SELL"
    private double amountCrypto;
    private double priceAtTransaction;
    private double totalValue;
    private LocalDateTime transactionDate;

    public Transaction() {
        this.transactionDate = LocalDateTime.now();
    }

    public Transaction(String id) {
    	this.id = id;
    }
    
    public Transaction(String userId, String cryptoId, String transactionType, double amountCrypto, double priceAtTransaction) {
        this.userId = userId;
        this.cryptoId = cryptoId;
        this.transactionType = transactionType;
        this.amountCrypto = amountCrypto;
        this.priceAtTransaction = priceAtTransaction;
        this.totalValue = amountCrypto * priceAtTransaction;
        this.transactionDate = LocalDateTime.now();
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

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public double getAmountCrypto() {
		return amountCrypto;
	}

	public void setAmountCrypto(double amountCrypto) {
		this.amountCrypto = amountCrypto;
	}

	public double getPriceAtTransaction() {
		return priceAtTransaction;
	}

	public void setPriceAtTransaction(double priceAtTransaction) {
		this.priceAtTransaction = priceAtTransaction;
	}

	public double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(double totalValue) {
		this.totalValue = totalValue;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}

    

}

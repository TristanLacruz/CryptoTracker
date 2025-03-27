package com.yolo.backend.mvc.model.services;

import java.util.List;

import com.yolo.backend.mvc.model.entity.Transaction;

public interface ITransactionService {

public List<Transaction> findAll();
	
	public void save(Transaction t);
	public Transaction findById(String id);
	public void delete(Transaction t);
	public Transaction update(Transaction t, String id);
	
	/*
	 * 
	 */
	Transaction buyCrypto(String userId, String cryptoId, double amountUSD);
	Transaction sellCrypto(String userId, String cryptoId, double amountCrypto);
	
}

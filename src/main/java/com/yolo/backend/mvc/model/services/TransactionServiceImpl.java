package com.yolo.backend.mvc.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yolo.backend.mvc.model.dao.ITransactionDAO;
import com.yolo.backend.mvc.model.entity.Transaction;
import com.yolo.backend.mvc.model.entity.User;
import com.yolo.backend.mvc.model.exceptions.TransactionNotFoundException;

@Service
public class TransactionServiceImpl implements ITransactionService {

	@Autowired
	private ITransactionDAO transactionDAO;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ICryptocurrencyService cryptoService;
	
	@Autowired
	private IPortfolioService portfolioService;

	@Override
	public List<Transaction> findAll() {
		return (List<Transaction>) transactionDAO.findAll();
	}

	@Override
	public void save(Transaction t) {
		transactionDAO.save(t);
	}

	@Override
	public Transaction findById(String id) {
		return transactionDAO.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
	}

	@Override
	public void delete(Transaction t) {
		transactionDAO.delete(t);
	}

	@Override
	public Transaction update(Transaction t, String id) {
		Transaction currentTransaction = this.findById(id);
		currentTransaction.setUserId(t.getUserId());
		currentTransaction.setTransactionType(t.getTransactionType());
		currentTransaction.setTransactionDate(t.getTransactionDate());
		currentTransaction.setTotalValue(t.getTotalValue());
		currentTransaction.setPriceAtTransaction(t.getPriceAtTransaction());
		currentTransaction.setCryptoId(t.getCryptoId());
		currentTransaction.setAmountCrypto(t.getAmountCrypto());
		return currentTransaction;
	}

	@Override
	public Transaction buyCrypto(String userId, String cryptoSymbol, double amountUSD) {
		User user = userService.findById(userId);
		double price = cryptoService.getCurrentPrice(cryptoSymbol);
		double quantity = amountUSD / price;

		if (user.getBalance() < amountUSD) {
			throw new RuntimeException("Saldo insuficiente.");
		}

		// Restar saldo
		user.setBalance(user.getBalance() - amountUSD);
		userService.save(user);

		// Añadir cripto al portfolio
		portfolioService.addCrypto(userId, cryptoSymbol, quantity);

		// Registrar transacción
		Transaction tx = new Transaction(userId, cryptoSymbol, "BUY", quantity, price);
		return transactionDAO.save(tx);
	}

	@Override
	public Transaction sellCrypto(String userId, String cryptoSymbol, double amountUSD) {
		User user = userService.findById(userId);
		double price = cryptoService.getCurrentPrice(cryptoSymbol);
		double quantity = amountUSD / price;

		if (!portfolioService.hasEnoughCrypto(userId, cryptoSymbol, quantity)) {
			throw new RuntimeException("No tienes suficiente cantidad de " + cryptoSymbol);
		}

		// Sumar saldo
		user.setBalance(user.getBalance() + amountUSD);
		userService.save(user);

		// Quitar cripto del portfolio
		portfolioService.removeCrypto(userId, cryptoSymbol, quantity);

		// Registrar transacción
		Transaction tx = new Transaction(userId, cryptoSymbol, "SELL", quantity, price);
		return transactionDAO.save(tx);
	}
}

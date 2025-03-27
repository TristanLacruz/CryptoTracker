package com.yolo.backend.mvc.model.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yolo.backend.mvc.model.dao.IPortfolioDAO;
import com.yolo.backend.mvc.model.entity.Portfolio;
import com.yolo.backend.mvc.model.exceptions.PortfolioNotFoundException;

@Service
public class PortfolioServiceImpl implements IPortfolioService {

	@Autowired
	private IPortfolioDAO portfolioDAO;

	@Override
	public List<Portfolio> findAll() {
		return (List<Portfolio>) portfolioDAO.findAll();
	}

	@Override
	public void save(Portfolio p) {
		portfolioDAO.save(p);
	}

	@Override
	public Portfolio findById(String id) {
		return portfolioDAO.findById(id).orElseThrow(() -> new PortfolioNotFoundException(id));
	}

	@Override
	public void delete(Portfolio p) {
		portfolioDAO.delete(p);
	}

	@Override
	public Portfolio update(Portfolio p, String id) {
		Portfolio currentPortfolio = this.findById(id);
		currentPortfolio.setUserId(p.getUserId());
		currentPortfolio.setCryptos(p.getCryptos());
		return currentPortfolio;
	}

	@Override
	public void addCrypto(String userId, String symbol, double quantity) {
		Portfolio portfolio = portfolioDAO.findByUserId(userId).orElse(new Portfolio(userId));

		portfolio.getCryptos().merge(symbol, quantity, Double::sum);
		portfolioDAO.save(portfolio);
	}

	@Override
	public void removeCrypto(String userId, String symbol, double quantity) {
		Portfolio portfolio = portfolioDAO.findByUserId(userId)
				.orElseThrow(() -> new RuntimeException("Portfolio no encontrado"));

		Map<String, Double> cryptos = portfolio.getCryptos();
		double currentAmount = cryptos.getOrDefault(symbol, 0.0);

		if (currentAmount < quantity) {
			throw new RuntimeException("No tienes suficiente cantidad de " + symbol);
		}

		double newAmount = currentAmount - quantity;

		if (newAmount == 0) {
			cryptos.remove(symbol);
		} else {
			cryptos.put(symbol, newAmount);
		}

		portfolioDAO.save(portfolio);
	}

	@Override
	public boolean hasEnoughCrypto(String userId, String symbol, double quantity) {
		Portfolio portfolio = portfolioDAO.findByUserId(userId).orElse(new Portfolio(userId));

		return portfolio.getCryptos().getOrDefault(symbol, 0.0) >= quantity;
	}

	@Override
	public Portfolio getPortfolioByUserId(String userId) {
		return portfolioDAO.findByUserId(userId).orElse(new Portfolio(userId));
	}
	
	@Override
	public void updatePortfolioAfterBuy(String userId, String cryptoId, double amountCrypto) {
	    Portfolio portfolio = portfolioDAO.findById(userId)
	            .orElseGet(() -> {
	                Portfolio newPortfolio = new Portfolio();
	                newPortfolio.setId(userId);
	                newPortfolio.setCryptos(new HashMap<>());
	                return newPortfolio;
	            });

	    Map<String, Double> cryptos = portfolio.getCryptos();

	    // Sumar la cantidad nueva a la ya existente (si existe)
	    double currentAmount = cryptos.getOrDefault(cryptoId, 0.0);
	    cryptos.put(cryptoId, currentAmount + amountCrypto);

	    portfolio.setCryptos(cryptos);
	    portfolioDAO.save(portfolio);
	}


}

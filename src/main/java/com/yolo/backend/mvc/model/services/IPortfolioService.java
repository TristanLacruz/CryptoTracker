package com.yolo.backend.mvc.model.services;

import java.util.List;
import com.yolo.backend.mvc.model.entity.Portfolio;

public interface IPortfolioService {

	public List<Portfolio> findAll();
	public void save(Portfolio p);	
	public Portfolio findById(String id);
	public void delete(Portfolio p);
	public Portfolio update(Portfolio p, String id);
	
	void addCrypto(String userId, String symbol, double quantity);
    void removeCrypto(String userId, String symbol, double quantity);
    boolean hasEnoughCrypto(String userId, String symbol, double quantity);
    Portfolio getPortfolioByUserId(String userId);
	void updatePortfolioAfterBuy(String userId, String cryptoId, double amountCrypto);
}

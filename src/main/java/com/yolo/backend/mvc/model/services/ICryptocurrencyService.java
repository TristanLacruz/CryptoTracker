package com.yolo.backend.mvc.model.services;

import java.util.List;
import java.util.Map;

import com.yolo.backend.mvc.model.entity.Cryptocurrency;

public interface ICryptocurrencyService {

	public List<Cryptocurrency> findAll();
	
	public void save(Cryptocurrency c);
	
	public Cryptocurrency findById(String id);
	
	public void delete(Cryptocurrency c);
	
	public Cryptocurrency update(Cryptocurrency c, String id);

	double getCurrentPrice(String symbol); // Ej: "bitcoin"
	
    Map<String, Object> getCryptoInfo(String symbol);
}

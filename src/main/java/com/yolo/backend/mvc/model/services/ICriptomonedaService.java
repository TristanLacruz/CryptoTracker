package com.yolo.backend.mvc.model.services;

import java.util.List;
import java.util.Map;

import com.yolo.backend.mvc.model.dto.CryptoMarketDTO;
import com.yolo.backend.mvc.model.entity.Criptomoneda;

public interface ICriptomonedaService {

	public List<Criptomoneda> findAll();
	
	public void save(Criptomoneda c);
	
	public Criptomoneda findById(String id);
	
	public void delete(Criptomoneda c);
	
	public Criptomoneda update(Criptomoneda c, String id);

	double getPrecioActual(String symbol); // Ej: "bitcoin"
	
    Map<String, Object> getCryptoInfo(String symbol);

    List<CryptoMarketDTO> getMarketData();

	List<Double> getHistoricalPrices(String cryptoId);

}

package com.tracker.backend.mvc.model.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.tracker.backend.mvc.model.entity.Criptomoneda;
import com.tracker.common.dto.CryptoMarketDTO;

public interface ICriptomonedaService {

	public List<Criptomoneda> findAll();
	
	public void save(Criptomoneda c);
	
	public Criptomoneda findById(String id);
	
	public void delete(Criptomoneda c);
	
	public Criptomoneda update(Criptomoneda c, String id);

	double obtenerPrecioActual(String simbolo);

	double getPrecioActual(String symbol); 
	
    Map<String, Object> getCryptoInfo(String symbol);

    List<CryptoMarketDTO> getMarketData();

    List<List<Double>> getHistoricalPrices(String cryptoId);
	
	List<Double> getHistoricalRSI(String cryptoId);

	double getPrecioEnFecha(String cryptoId, LocalDate fecha);

}

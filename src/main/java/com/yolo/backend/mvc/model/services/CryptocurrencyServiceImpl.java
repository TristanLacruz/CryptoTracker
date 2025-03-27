package com.yolo.backend.mvc.model.services;

import java.util.List;
import java.util.Map;

import com.yolo.backend.mvc.model.dao.ICryptocurrencyDAO;
import com.yolo.backend.mvc.model.entity.Cryptocurrency;
import com.yolo.backend.mvc.model.exceptions.CryptocurrencyNotFoundException;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CryptocurrencyServiceImpl implements ICryptocurrencyService {

	private static final String BASE_URL = "https://pro-api.coingecko.com/api/v3";	

    private final RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private ICryptocurrencyDAO cryptoDAO;

	@Value("${coingecko.api.key}")
	private String apiKey;

    
    @PostConstruct
    public void testApiKey() {
        System.out.println("API Key cargada correctamente: " + apiKey);
    }

	
	@Override
	public List<Cryptocurrency> findAll() {
		return (List<Cryptocurrency>)cryptoDAO.findAll();
	}

	@Override
	public void save(Cryptocurrency c) {
		cryptoDAO.save(c);
	}

	@Override
	public Cryptocurrency findById(String id) {
		return cryptoDAO.findById(id)
				.orElseThrow(() -> new CryptocurrencyNotFoundException(id));
	}

	@Override
	public void delete(Cryptocurrency c) {
		cryptoDAO.delete(c);
	}

	@Override
	public Cryptocurrency update(Cryptocurrency c, String id) {
		Cryptocurrency currentCrypto = this.findById(id);
		currentCrypto.setName(c.getName());
		currentCrypto.setSymbol(c.getSymbol());
		currentCrypto.setCurrentPrice(c.getCurrentPrice());
		currentCrypto.setLastUpdated(c.getLastUpdated());
		this.save(currentCrypto);
		return currentCrypto;
	}
	
	
	@Override
	public double getCurrentPrice(String symbol) {
		String url = BASE_URL + "/simple/price?ids=" + symbol + "&vs_currencies=usd";

		Map<String, Map<String, Double>> data = restTemplate.getForObject(url, Map.class);

		if (data != null && data.containsKey(symbol)) {
		    return data.get(symbol).get("usd");
		}


	    throw new RuntimeException("No se pudo obtener el precio para: " + symbol);
	}




    @Override
    public Map<String, Object> getCryptoInfo(String symbol) {
    	String url = "https://api.coingecko.com/api/v3/simple/price?ids=" 
                + symbol + "&vs_currencies=usd"
                + "&x_cg_demo_api_key=" + apiKey;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> data = response.getBody();

        if (data != null) {
            return data;
        }

        throw new RuntimeException("No se pudo obtener información para: " + symbol);
    }


}

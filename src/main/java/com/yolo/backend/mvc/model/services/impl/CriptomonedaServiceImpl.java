package com.yolo.backend.mvc.model.services.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.yolo.backend.mvc.model.dao.ICriptomonedaDAO;
import com.yolo.backend.mvc.model.dto.CryptoMarketDTO;
import com.yolo.backend.mvc.model.entity.Criptomoneda;
import com.yolo.backend.mvc.model.exceptions.CriptomonedaNoEncontradaException;
import com.yolo.backend.mvc.model.services.ICriptomonedaService;

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
public class CriptomonedaServiceImpl implements ICriptomonedaService {

	private static final String BASE_URL = "https://pro-api.coingecko.com/api/v3";	

    private final RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private ICriptomonedaDAO cryptoDAO;

	@Value("${coingecko.api.key}")
	private String apiKey;

    
    @PostConstruct
    public void testApiKey() {
        System.out.println("API Key cargada correctamente: " + apiKey);
    }

	
	@Override
	public List<Criptomoneda> findAll() {
		return (List<Criptomoneda>)cryptoDAO.findAll();
	}

	@Override
	public void save(Criptomoneda c) {
		cryptoDAO.save(c);
	}

	@Override
	public Criptomoneda findById(String id) {
		return cryptoDAO.findById(id)
				.orElseThrow(() -> new CriptomonedaNoEncontradaException(id));
	}

	@Override
	public void delete(Criptomoneda c) {
		cryptoDAO.delete(c);
	}

	@Override
	public Criptomoneda update(Criptomoneda c, String id) {
		Criptomoneda currentCrypto = this.findById(id);
		currentCrypto.setNombre(c.getNombre());
		currentCrypto.setSimbolo(c.getSimbolo());
		currentCrypto.setPrecioActual(c.getPrecioActual());
		currentCrypto.setUltimaActualizacion(c.getUltimaActualizacion());
		this.save(currentCrypto);
		return currentCrypto;
	}
	
	
	@Override
	public double getPrecioActual(String simbolo) {
		String url = BASE_URL + "/simple/price?ids=" + simbolo + "&vs_currencies=usd";

		Map<String, Map<String, Double>> data = restTemplate.getForObject(url, Map.class);

		if (data != null && data.containsKey(simbolo)) {
		    return data.get(simbolo).get("usd");
		}


	    throw new RuntimeException("No se pudo obtener el precio para: " + simbolo);
	}




    @Override
    public Map<String, Object> getCryptoInfo(String simbolo) {
    	String url = "https://api.coingecko.com/api/v3/simple/price?ids=" 
                + simbolo + "&vs_currencies=usd"
                + "&x_cg_demo_api_key=" + apiKey;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> data = response.getBody();

        if (data != null) {
            return data;
        }

        throw new RuntimeException("No se pudo obtener información para: " + simbolo);
    }

    @Override
    public List<CryptoMarketDTO> getMarketData() {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=eur&order=market_cap_desc&per_page=50&page=1&sparkline=false";

        ResponseEntity<CryptoMarketDTO[]> response = restTemplate.getForEntity(url, CryptoMarketDTO[].class);
        return Arrays.asList(response.getBody());
    }
}

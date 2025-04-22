package com.yolo.backend.mvc.model.services.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolo.backend.indicadores.RSIUtil;
import com.yolo.backend.mvc.model.dao.ICriptomonedaDAO;
import com.yolo.backend.mvc.model.dto.CryptoMarketDTO;
import com.yolo.backend.mvc.model.entity.Criptomoneda;
import com.yolo.backend.mvc.model.exceptions.CriptomonedaNoEncontradaException;
import com.yolo.backend.mvc.model.services.ICriptomonedaService;

import jakarta.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CriptomonedaServiceImpl implements ICriptomonedaService {

	private static final String BASE_URL = "https://api.coingecko.com/api/v3";

	private final RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private ICriptomonedaDAO cryptoDAO;

	@Value("${coingecko.api.key}")
	private String apiKey;

	private final Map<String, List<Double>> precioCache = new HashMap<>();

	private final HttpClient httpClient = HttpClient.newHttpClient();

	@PostConstruct
	public void testApiKey() {
		System.out.println("API Key cargada correctamente: " + apiKey);
	}


	@Override
	public List<Criptomoneda> findAll() {
		return (List<Criptomoneda>) cryptoDAO.findAll();
	}

	@Override
	public void save(Criptomoneda c) {
		cryptoDAO.save(c);
	}

	@Override
	public Criptomoneda findById(String id) {
		return cryptoDAO.findById(id).orElseThrow(() -> new CriptomonedaNoEncontradaException(id));
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
		String url = BASE_URL + "/simple/price?ids=" + simbolo + "&vs_currencies=usd";

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("accept", "application/json")
//				.header("x-cg-demo-api-key", apiKey)
				.build();

		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 403) {
			    System.err.println("❌ Error 403: CoinGecko ha denegado el acceso. Verifica que la API Key esté correcta y que se esté enviando como 'x-cg-demo-api-key'.");
			    throw new RuntimeException("Error en la respuesta: 403");
			}

			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> data = mapper.readValue(response.body(), Map.class);
			return data;
		} catch (Exception e) {
			throw new RuntimeException("No se pudo obtener información para: " + simbolo + " -> " + e.getMessage());
		}
	}

	private List<CryptoMarketDTO> cache = new ArrayList<>();
	private long lastUpdate = 0;

	@Override
	public List<CryptoMarketDTO> getMarketData() {
		long now = System.currentTimeMillis();
		if (!cache.isEmpty() && (now - lastUpdate) < 60_000) {
			return cache;
		}

		try {
			String url = BASE_URL
				    + "/coins/markets?vs_currency=eur&order=market_cap_desc&per_page=50&page=1&sparkline=false";

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("accept", "application/json")
//					.header("x-cg-demo-api-key", apiKey)
					.build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 401) {
			    System.err.println("⚠️ Clave API rechazada. Usando datos vacíos de emergencia.");
			    return Collections.emptyList(); // o lista cacheada antigua si prefieres
			}

			System.out.println("📶 Código HTTP: " + response.statusCode());
			System.out.println("📦 Respuesta raw de CoinGecko:\n" + response.body());

			if (response.statusCode() != 200) {
				System.err.println("❌ Error: CoinGecko devolvió código " + response.statusCode());
				return Collections.emptyList();
			}

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			CryptoMarketDTO[] body = mapper.readValue(response.body(), CryptoMarketDTO[].class);
			cache = Arrays.asList(body);
			lastUpdate = now;

			return cache;

		} catch (Exception e) {
			System.err.println("❌ Excepción al obtener datos del mercado: " + e.getMessage());
			return Collections.emptyList();
		}
	}


	@Override
	public List<List<Double>> getHistoricalPrices(String id) {
	    try {
	        String url = BASE_URL + "/coins/" + id + "/market_chart?vs_currency=eur&days=30&interval=daily";

	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("accept", "application/json")
//	            .header("x-cg-demo-api-key", apiKey)  // ✅ CLAVE DEMO
	            .build();

	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	        System.out.println("🟡 Respuesta CoinGecko:\n" + response.body());

	        JSONObject json = new JSONObject(response.body());
	        JSONArray priceArray = json.getJSONArray("prices");

	        List<List<Double>> historicalPrices = new ArrayList<>();
	        for (int i = 0; i < priceArray.length(); i++) {
	            JSONArray priceEntry = priceArray.getJSONArray(i);
	            List<Double> pricePoint = new ArrayList<>();
	            pricePoint.add(priceEntry.getDouble(0)); // timestamp
	            pricePoint.add(priceEntry.getDouble(1)); // price
	            historicalPrices.add(pricePoint);
	        }

	        return historicalPrices;
	    } catch (Exception e) {
	        System.out.println("❌ Error al obtener precios históricos: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}




	@Override
	@Cacheable("historicalPrices")
	public List<Double> getHistoricalRSI(String id) {
	    try {
	    	String url = BASE_URL + "/coins/" + id + "/market_chart?vs_currency=eur&days=30&interval=daily";

	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(url))
	                .header("accept", "application/json")
//	                .header("x-cg-demo-api-key", apiKey)
	                .build();

	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println("🟡 Respuesta CoinGecko:\n" + response.body());
			
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode root = mapper.readTree(response.body());
	        JsonNode pricesNode = root.get("prices");

	        List<Double> prices = new ArrayList<>();
	        for (JsonNode price : pricesNode) {
	            prices.add(price.get(1).asDouble()); // precio está en la posición 1
	        }

	        return RSIUtil.calculateRSIList(prices, 14); // Devuelve lista con RSI
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Collections.emptyList();
	    }
	}


	private List<Double> fetchPriceHistoryFromAPI(String cryptoId) {
	    List<Double> prices = new ArrayList<>();

	    try {
	        String url = BASE_URL + "/coins/" + cryptoId + "/market_chart?vs_currency=eur&days=7&interval=daily";

	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(url))
	                .header("accept", "application/json")
//	                .header("x-cg-demo-api-key", apiKey) // CORRECTO
	                .build();

	        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
	        System.out.println("🟡 Respuesta CoinGecko:\n" + response.body());

	        JSONObject json = new JSONObject(response.body());
	        JSONArray priceArray = json.getJSONArray("prices");

	        for (int i = 0; i < priceArray.length(); i++) {
	            JSONArray entry = priceArray.getJSONArray(i);
	            prices.add(entry.getDouble(1)); // El precio está en la segunda posición
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return prices;
	}




}

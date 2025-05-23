package com.tracker.backend.mvc.model.services.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.backend.indicadores.RSIUtil;
import com.tracker.backend.mvc.model.dao.ICriptomonedaDAO;
import com.tracker.common.dto.CryptoMarketDTO;
import com.tracker.backend.mvc.model.entity.Criptomoneda;
import com.tracker.backend.mvc.model.exceptions.CriptomonedaNoEncontradaException;
import com.tracker.backend.mvc.model.services.ICriptomonedaService;
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

	private final Map<String, List<List<Double>>> historicalCache = new HashMap<>();
	private final long CACHE_TTL = 5 * 60 * 1000; // 5 minutos en milisegundos
	private final Map<String, Long> cacheTimestamps = new HashMap<>();

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
	    Map<String, String> simboloToId = Map.ofEntries(
	        Map.entry("BTC", "bitcoin"),
	        Map.entry("ETH", "ethereum"),
	        Map.entry("BNB", "binancecoin"),
	        Map.entry("ADA", "cardano"),
	        Map.entry("XRP", "ripple"),
	        Map.entry("USDT", "tether"),
	        Map.entry("DOGE", "dogecoin"),
	        Map.entry("DOT", "polkadot"),
	        Map.entry("SOL", "solana"),
	        Map.entry("USDC", "usd-coin")
	    );

	    String id = simboloToId.getOrDefault(simbolo.toUpperCase(), simbolo.toLowerCase());
	    String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + id + "&vs_currencies=eur";

	    try {
	        HttpClient client = HttpClient.newHttpClient();
	        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	        JSONObject json = new JSONObject(response.body());
	        return json.getJSONObject(id).getDouble("eur");
	    } catch (Exception e) {
	        System.err.println("Error al obtener precio para " + simbolo + " (ID: " + id + "): " + e.getMessage());
	        return 0.0;
	    }
	}

	@Override
	public Map<String, Object> getCryptoInfo(String simbolo) {
		String url = BASE_URL + "/simple/price?ids=" + simbolo + "&vs_currencies=usd";

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("accept", "application/json").build();

		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 403) {
				System.err.println(
						"Error 403: CoinGecko ha denegado el acceso. Verifica que la API Key esté correcta y que se esté enviando como 'x-cg-demo-api-key'.");
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
					+ "/coins/markets?vs_currency=eur&order=market_cap_desc&per_page=25&page=1&sparkline=false";

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("accept", "application/json").build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 401) {
				System.err.println("Clave API rechazada. Usando datos vacíos de emergencia.");
				return Collections.emptyList(); 
			}

			System.out.println("📶 Código HTTP: " + response.statusCode());
			//System.out.println("Respuesta raw de CoinGecko:\n" + response.body());

			if (response.statusCode() != 200) {
				System.err.println("Error: CoinGecko devolvió código " + response.statusCode());
				return Collections.emptyList();
			}

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			CryptoMarketDTO[] body = mapper.readValue(response.body(), CryptoMarketDTO[].class);
			cache = Arrays.asList(body);
			lastUpdate = now;

			return cache;

		} catch (Exception e) {
			System.err.println("Excepción al obtener datos del mercado: " + e.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public List<List<Double>> getHistoricalPrices(String id) {
		long now = System.currentTimeMillis();

		// Usar caché si existe y no está caducada
		if (historicalCache.containsKey(id)) {
			long lastFetch = cacheTimestamps.getOrDefault(id, 0L);
			if (now - lastFetch < CACHE_TTL) {
				System.out.println("Usando precios históricos en caché para: " + id);
				return historicalCache.get(id);
			}
		}

		try {
			String url = BASE_URL + "/coins/" + id + "/market_chart?vs_currency=eur&days=50&interval=daily";
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("accept", "application/json").build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			//System.out.println("Respuesta CoinGecko:\n" + response.body());

			JSONObject json = new JSONObject(response.body());
			//System.out.println("JSON recibido: " + response.body());
			JSONArray priceArray = json.getJSONArray("prices");

			List<List<Double>> historicalPrices = new ArrayList<>();
			for (int i = 0; i < priceArray.length(); i++) {
				JSONArray priceEntry = priceArray.getJSONArray(i);
				List<Double> pricePoint = new ArrayList<>();
				pricePoint.add(priceEntry.getDouble(0)); // fecha
				pricePoint.add(priceEntry.getDouble(1)); // precio
				historicalPrices.add(pricePoint);
			}

			// Guardar en caché
			historicalCache.put(id, historicalPrices);
			cacheTimestamps.put(id, now);

			return historicalPrices;

		} catch (Exception e) {
			System.out.println("Error al obtener precios históricos para " + id + ": " + e.getMessage());

			// Si hubo error, devolver datos anteriores en caché si los hay
			return historicalCache.getOrDefault(id, Collections.emptyList());
		}
	}

	@Override
	@Cacheable("historicalPrices")
	public List<Double> getHistoricalRSI(String id) {
		try {
			String url = BASE_URL + "/coins/" + id + "/market_chart?vs_currency=eur&days=30&interval=daily";

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("accept", "application/json").build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println("Respuesta CoinGecko:\n" + response.body());

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response.body());
			JsonNode pricesNode = root.get("prices");

			List<Double> prices = new ArrayList<>();
			for (JsonNode price : pricesNode) {
				prices.add(price.get(1).asDouble()); 
			}

			return RSIUtil.calculateRSIList(prices, 14);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	private List<Double> fetchPriceHistoryFromAPI(String cryptoId) {
		List<Double> prices = new ArrayList<>();

		try {
			String url = BASE_URL + "/coins/" + cryptoId + "/market_chart?vs_currency=eur&days=7&interval=daily";

			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).header("accept", "application/json").build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println("Respuesta CoinGecko:\n" + response.body());

			JSONObject json = new JSONObject(response.body());
			//System.out.println("JSON recibido: " + response.body());
			JSONArray priceArray = json.getJSONArray("prices");

			for (int i = 0; i < priceArray.length(); i++) {
				JSONArray entry = priceArray.getJSONArray(i);
				prices.add(entry.getDouble(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return prices;
	}

	@Override
	public double getPrecioEnFecha(String cryptoId, LocalDate fecha) {
	    try {
	        long timestamp = fecha.atStartOfDay(ZoneId.of("UTC")).toEpochSecond();
	        String url = "https://api.coingecko.com/api/v3/coins/" + cryptoId +
	                "/market_chart/range?vs_currency=eur&from=" + timestamp + "&to=" + (timestamp + 86400);

	        HttpClient client = HttpClient.newHttpClient();
	        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	        //System.out.println("JSON recibido: " + response.body());

	        JSONObject json = new JSONObject(response.body());

	        if (!json.has("prices")) {
	            throw new RuntimeException("La respuesta no contiene 'prices':\n" + json.toString(2));
	        }

	        JSONArray prices = json.getJSONArray("prices");

	        if (prices.length() > 0) {
	            return prices.getJSONArray(0).getDouble(1);
	        }

	    } catch (Exception e) {
	        System.err.println("Error obteniendo precio histórico de " + cryptoId + ": " + e.getMessage());
	    }

	    return 0;
	}


	@Override
	public double obtenerPrecioActual(String simbolo) {
		// TODO Auto-generated method stub
		return 0;
	}

}

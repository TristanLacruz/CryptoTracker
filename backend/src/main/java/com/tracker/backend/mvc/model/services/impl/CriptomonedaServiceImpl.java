package com.tracker.backend.mvc.model.services.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
import com.tracker.backend.mvc.model.dao.IPortafolioDAO;
import com.tracker.common.dto.CryptoMarketDTO;
import com.tracker.backend.mvc.model.entity.Criptomoneda;
import com.tracker.backend.mvc.model.entity.Portafolio;
import com.tracker.backend.mvc.model.exceptions.CriptomonedaNoEncontradaException;
import com.tracker.backend.mvc.model.services.ICriptomonedaService;
import jakarta.annotation.PostConstruct;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/*
 * Servicio que implementa la lógica de negocio para manejar criptomonedas.
 */
@Service
public class CriptomonedaServiceImpl implements ICriptomonedaService {

	private static final String BASE_URL = "https://api.coingecko.com/api/v3";

	private final Map<String, CachedPrice> cache = new HashMap<>();

	@Autowired
	private ICriptomonedaDAO cryptoDAO;

	@Autowired
	private IPortafolioDAO portafolioDAO;

	@Value("${coingecko.api.key}")
	private String apiKey;

	private final HttpClient httpClient = HttpClient.newHttpClient();

	private final Map<String, List<List<Double>>> historicalCache = new HashMap<>();

	private final long CACHE_TTL = 5 * 60 * 1000; // 5 minutos en milisegundos

	private final Map<String, Long> cacheTimestamps = new HashMap<>();

	private List<CryptoMarketDTO> marketCache = new ArrayList<>();

	private long lastUpdate = 0;

	/**
	 * Método que se ejecuta al iniciar la aplicación para verificar que la API Key
	 * se ha cargado correctamente.
	 */
	@PostConstruct
	public void testApiKey() {
		System.out.println("API Key cargada correctamente: " + apiKey);
	}

	/*
	 * Método que devuelve todas las criptomonedas almacenadas en la base de datos.
	 */
	@Override
	public List<Criptomoneda> findAll() {
		return (List<Criptomoneda>) cryptoDAO.findAll();
	}

	/*
	 * Método que guarda una nueva criptomoneda en la base de datos.
	 */
	@Override
	public void save(Criptomoneda c) {
		cryptoDAO.save(c);
	}

	/*
	 * Método que busca una criptomoneda por su ID.
	 * Si no se encuentra, lanza una excepción personalizada.
	 */
	@Override
	public Criptomoneda findById(String id) {
		return cryptoDAO.findById(id).orElseThrow(() -> new CriptomonedaNoEncontradaException(id));
	}

	/*
	 * Método que elimina una criptomoneda de la base de datos.
	 */
	@Override
	public void delete(Criptomoneda c) {
		cryptoDAO.delete(c);
	}

	/*
	 * Método que actualiza una criptomoneda existente.
	 */
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

	/**
	 * Método que obtiene el precio actual de una criptomoneda utilizando la API de CoinGecko.
	 * Utiliza un caché para evitar llamadas excesivas a la API.
	 *
	 * @param simbolo El símbolo de la criptomoneda (ej. "BTC", "ETH").
	 * @return El precio actual en euros.
	 */
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
				Map.entry("USDC", "usd-coin"));

		String id = simboloToId.getOrDefault(simbolo.toUpperCase(), simbolo.toLowerCase());

		CachedPrice cached = cache.get(id);
		long now = System.currentTimeMillis();
		if (cached != null && (now - cached.timestamp) < 60_000) {
			return cached.precio;
		}

		String url = "https://api.coingecko.com/api/v3/simple/price?ids=" + id + "&vs_currencies=eur";

		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("accept", "application/json")
					.header("x-cg-demo-api-key", "CG-Scvsy3fknroJfMkawrMffRv8n")
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200) {
				System.err.println("CoinGecko rechazó la petición: " + response.statusCode());
				System.err.println("Respuesta: " + response.body());
				return 1.0;
			}

			JSONObject json = new JSONObject(response.body());
			double precio = json.getJSONObject(id).getDouble("eur");

			cache.put(id, new CachedPrice(precio, now));
			return precio;

		} catch (Exception e) {
			System.err.println("Error al obtener precio para " + simbolo + " (ID: " + id + "): " + e.getMessage());
			return 1.0;
		}
	}

	/**
	 * Método que obtiene información detallada de una criptomoneda utilizando la API de CoinGecko.
	 * Utiliza un caché para evitar llamadas excesivas a la API.
	 *
	 * @param simbolo El símbolo de la criptomoneda (ej. "bitcoin", "ethereum").
	 * @return Un mapa con la información de la criptomoneda.
	 */
	@Override
	public Map<String, Object> getCryptoInfo(String simbolo) {
		String url = BASE_URL + "/simple/price?ids=" + simbolo + "&vs_currencies=usd";

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("accept", "application/json")
				.build();

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

	/**
	 * Método que obtiene datos del mercado de criptomonedas desde CoinGecko.
	 * Utiliza un caché para evitar llamadas excesivas a la API.
	 *
	 * @return Una lista de objetos CryptoMarketDTO con los datos del mercado.
	 */
	@Override
	public List<CryptoMarketDTO> getMarketData() {
		long now = System.currentTimeMillis();
		if (!marketCache.isEmpty() && (now - lastUpdate) < 60_000) {
			return marketCache;
		}

		try {
			String url = BASE_URL
					+ "/coins/markets?vs_currency=eur&order=market_cap_desc&per_page=10&page=1&sparkline=false";

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("accept", "application/json")
					.header("x-cg-demo-api-key", apiKey)
					.build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			System.out.println("Llamando a CoinGecko: " + url);
			System.out.println("Código de respuesta: " + response.statusCode());

			if (response.statusCode() != 200) {
				System.err.println("Error: CoinGecko devolvió código " + response.statusCode());
				return Collections.emptyList();
			}

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			CryptoMarketDTO[] body = mapper.readValue(response.body(), CryptoMarketDTO[].class);
			marketCache = Arrays.asList(body);
			lastUpdate = now;

			return marketCache;

		} catch (Exception e) {
			System.err.println("Excepción al obtener datos del mercado: " + e.getMessage());
			return Collections.emptyList();
		}
	}

	/**
	 * Método que obtiene los precios históricos de una criptomoneda.
	 * Utiliza un caché para evitar llamadas excesivas a la API.
	 *
	 * @param id El ID de la criptomoneda (ej. "bitcoin", "ethereum").
	 * @return Una lista de listas con los precios históricos.
	 */
	@Override
	public List<List<Double>> getHistoricalPrices(String id) {
		long now = System.currentTimeMillis();

		if (historicalCache.containsKey(id)) {
			long lastFetch = cacheTimestamps.getOrDefault(id, 0L);
			if (now - lastFetch < CACHE_TTL) {
				System.out.println("Usando precios históricos en caché para: " + id);
				return historicalCache.get(id);
			}
		}

		try {
			String url = BASE_URL + "/coins/" + id + "/market_chart?vs_currency=eur&days=50&interval=daily";
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("accept", "application/json")
					.header("x-cg-demo-api-key", apiKey)
					.build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200) {
				System.err.println("CoinGecko devolvió código " + response.statusCode() + " para " + id);
				return historicalCache.getOrDefault(id, Collections.emptyList());
			}

			JSONObject json = new JSONObject(response.body());

			if (!json.has("prices")) {
				System.err.println("El JSON de CoinGecko no contiene el campo 'prices' para " + id);
				System.err.println("Respuesta completa: " + json.toString(2));
				return historicalCache.getOrDefault(id, Collections.emptyList());
			}

			JSONArray priceArray = json.getJSONArray("prices");
			List<List<Double>> historicalPrices = new ArrayList<>();

			for (int i = 0; i < priceArray.length(); i++) {
				JSONArray priceEntry = priceArray.getJSONArray(i);
				List<Double> pricePoint = List.of(
						priceEntry.getDouble(0),
						priceEntry.getDouble(1));
				historicalPrices.add(pricePoint);
			}

			historicalCache.put(id, historicalPrices);
			cacheTimestamps.put(id, now);

			return historicalPrices;

		} catch (Exception e) {
			System.err.println("Error al obtener precios históricos para " + id + ": " + e.getMessage());
			return historicalCache.getOrDefault(id, Collections.emptyList());
		}
	}

	/**
	 * Método que obtiene el RSI (Relative Strength Index) histórico de una criptomoneda.
	 * Utiliza un caché para evitar llamadas excesivas a la API.
	 *
	 * @param id El ID de la criptomoneda (ej. "bitcoin", "ethereum").
	 * @return Una lista con los valores del RSI calculados.
	 */
	@Override
	@Cacheable("historicalPrices")
	public List<Double> getHistoricalRSI(String id) {
		try {
			String url = BASE_URL + "/coins/" + id + "/market_chart?vs_currency=eur&days=30&interval=daily";

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("accept", "application/json")
					.header("x-cg-demo-api-key", apiKey)
					.build();

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

	/**
	 * Método que obtiene el historial de precios de una criptomoneda.
	 * Utiliza un caché para evitar llamadas excesivas a la API.
	 *
	 * @param cryptoId El ID de la criptomoneda (ej. "bitcoin", "ethereum").
	 * @return Una lista con los precios históricos.
	 */
	private List<Double> fetchPriceHistoryFromAPI(String cryptoId) {
		List<Double> prices = new ArrayList<>();

		try {
			String url = BASE_URL + "/coins/" + cryptoId + "/market_chart?vs_currency=eur&days=7&interval=daily";

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("accept", "application/json")
					.header("x-cg-demo-api-key", apiKey)
					.build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			System.out.println("Respuesta CoinGecko:\n" + response.body());

			JSONObject json = new JSONObject(response.body());
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

	/**
	 * Método que obtiene el precio de una criptomoneda en una fecha específica.
	 * Utiliza la API de CoinGecko para obtener el precio histórico.
	 *
	 * @param cryptoId El ID de la criptomoneda (ej. "bitcoin", "ethereum").
	 * @param fecha    La fecha para la que se desea obtener el precio.
	 * @return El precio de la criptomoneda en esa fecha, o 0 si no se pudo obtener.
	 */
	@Override
	public double getPrecioEnFecha(String cryptoId, LocalDate fecha) {
		try {
			long timestamp = fecha.atStartOfDay(ZoneId.of("UTC")).toEpochSecond();
			String url = "https://api.coingecko.com/api/v3/coins/" + cryptoId +
					"/market_chart/range?vs_currency=eur&from=" + timestamp + "&to=" + (timestamp + 86400);

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.header("x-cg-demo-api-key", apiKey)
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

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

	/**
	 * Método que calcula el valor actual de las criptomonedas en el portafolio de un usuario.
	 * Recorre las criptomonedas del portafolio y multiplica la cantidad por el precio actual.
	 *
	 * @param usuarioId El ID del usuario cuyo portafolio se desea calcular.
	 * @return El valor total en euros de las criptomonedas en el portafolio.
	 */
	@Override
	public double calcularValorActualEnCriptos(String usuarioId) {
		Portafolio portafolio = portafolioDAO.findByUsuarioId(usuarioId)
				.orElseThrow(() -> new RuntimeException("Portafolio no encontrado"));

		double valorCriptos = 0.0;

		for (Map.Entry<String, Double> entry : portafolio.getCriptomonedas().entrySet()) {
			String simbolo = entry.getKey();
			double cantidad = entry.getValue();
			double precio = getPrecioActual(simbolo);
			valorCriptos += cantidad * precio;
		}

		return valorCriptos;
	}

	/**
	 * Clase interna para almacenar precios en caché con su timestamp.
	 */
	private static class CachedPrice {
		double precio;
		long timestamp;

		CachedPrice(double precio, long timestamp) {
			this.precio = precio;
			this.timestamp = timestamp;
		}
	}
}

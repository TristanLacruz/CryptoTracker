package com.yolo.backend.mvc.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.backend.indicadores.RSIUtil;
import com.yolo.backend.mvc.model.dto.CryptoMarketDTO;
import com.yolo.backend.mvc.model.entity.Criptomoneda;
import com.yolo.backend.mvc.model.services.ICriptomonedaService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/cryptos")
public class CriptomonedaRestController {

	@Autowired
	private ICriptomonedaService criptomonedaService;

	public CriptomonedaRestController(ICriptomonedaService criptomonedaService) {
		this.criptomonedaService = criptomonedaService;
	}

	@GetMapping("")
	public List<Criptomoneda> getCryptos() {
		return criptomonedaService.findAll();
	}

	@PostMapping("")
	@ResponseStatus(HttpStatus.CREATED)
	public Criptomoneda createCryptos(@RequestBody Criptomoneda crypto) {
		criptomonedaService.save(crypto);
		return crypto;
	}

	@GetMapping("/precio/{simbolo}")
	public double getPrice(@PathVariable String simbolo) {
		return criptomonedaService.getPrecioActual(simbolo.toLowerCase());
	}

	@GetMapping("/info/{simbolo}")
	public Map<String, Object> getInfo(@PathVariable String simbolo) {
		return criptomonedaService.getCryptoInfo(simbolo.toLowerCase());
	}

	@GetMapping("/market")
	public List<CryptoMarketDTO> getMarketCryptos() {
		return criptomonedaService.getMarketData();
	}
	
	
	@GetMapping("/{id}/historical")
	public ResponseEntity<Map<String, Object>> getHistoricalPrices(@PathVariable String id) {
	    List<List<Double>> prices = criptomonedaService.getHistoricalPrices(id);

	    Map<String, Object> response = new HashMap<>();
	    response.put("prices", prices);

	    return ResponseEntity.ok(response);
	}



	@GetMapping("/{id}/rsi")
	public ResponseEntity<?> getRSI(@PathVariable String id) {
	    List<Double> precios = criptomonedaService.getHistoricalPrices(id)
	        .stream()
	        .map(pair -> pair.get(1))
	        .collect(Collectors.toList());

	    if (precios.size() < 15) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("No hay suficientes datos para calcular el RSI.");
	    }

	    double rsi = RSIUtil.calcularRSI(precios.subList(0, 15));
	    return ResponseEntity.ok(rsi);
	}


	
	
	@GetMapping("/{id}/rsi/history")
	public ResponseEntity<List<Double>> getRsiHistory(@PathVariable String id) {
		List<Double> precios = criptomonedaService.getHistoricalPrices(id)
                .stream()
                .map(pair -> pair.get(1)) // toma solo el precio
                .collect(Collectors.toList());
	    List<Double> rsi = RSIUtil.calculateRSIList(precios, 14); // ✅
	    return ResponseEntity.ok(rsi);
	}
	
	@GetMapping("/{id}/price/history")
	public ResponseEntity<List<List<Double>>> getPriceHistory(@PathVariable String id) {
	    List<List<Double>> historicalPrices = criptomonedaService.getHistoricalPrices(id);

	    if (historicalPrices.isEmpty()) {
	        System.out.println("No se encontraron datos históricos para: " + id);
	    }

	    return ResponseEntity.ok(historicalPrices);
	}
	
	




}

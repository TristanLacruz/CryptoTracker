package com.tracker.backend.mvc.controllers;

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
import com.tracker.backend.indicadores.EMAUtil;
import com.tracker.backend.indicadores.MACDUtil;
import com.tracker.backend.indicadores.RSIUtil;
import com.tracker.backend.indicadores.SMAUtil;
import com.tracker.common.dto.CompraRequestDTO;
import com.tracker.common.dto.CryptoMarketDTO;
import com.tracker.common.dto.VentaRequestDTO;
import com.tracker.backend.mvc.model.entity.Criptomoneda;
import com.tracker.backend.mvc.model.entity.Transaccion;
import com.tracker.backend.mvc.model.services.ICriptomonedaService;
import com.tracker.backend.mvc.model.services.ITransaccionService;
import com.tracker.backend.mvc.model.services.impl.TransaccionServiceImpl;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/cryptos")
public class CriptomonedaRestController {

	@Autowired
	private ICriptomonedaService criptomonedaService;
	
	@Autowired
	private ITransaccionService transaccionService;

	
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

	    List<Double> rsiList = RSIUtil.calculateRSIList(precios, 14);
	    double rsi = rsiList.get(rsiList.size() - 1);

	    return ResponseEntity.ok(rsi);
	}
	
	@GetMapping("/{id}/sma")
	public ResponseEntity<List<Double>> getSMA(@PathVariable String id) {
	    List<Double> precios = criptomonedaService.getHistoricalPrices(id)
	        .stream()
	        .map(pair -> pair.get(1))
	        .collect(Collectors.toList());
	    
	    if (precios.size() < 7) {
	        return ResponseEntity.badRequest().body(List.of());
	    }

	    List<Double> sma = SMAUtil.calcularSMA(precios, 7);
	    return ResponseEntity.ok(sma);
	}

	@GetMapping("/{id}/ema")
	public ResponseEntity<List<Double>> getEMA(@PathVariable String id) {
	    List<Double> precios = criptomonedaService.getHistoricalPrices(id)
	        .stream()
	        .map(pair -> pair.get(1))
	        .collect(Collectors.toList());

	    if (precios.size() < 7) {
	        return ResponseEntity.badRequest().body(List.of());
	    }

	    List<Double> ema = EMAUtil.calcularEMA(precios, 7);
	    return ResponseEntity.ok(ema);
	}

	@GetMapping("/{id}/macd")
	public ResponseEntity<Map<String, List<Double>>> getMACD(@PathVariable String id) {
	    List<Double> precios = criptomonedaService.getHistoricalPrices(id)
	        .stream()
	        .map(pair -> pair.get(1))
	        .collect(Collectors.toList());

	    if (precios.size() < 35) {
	        return ResponseEntity.badRequest().body(Map.of("macd", List.of(), "signal", List.of()));
	    }

	    Map<String, List<Double>> macdData = MACDUtil.calcularMACD(precios);

	    if (macdData.get("macd").isEmpty() || macdData.get("signal").isEmpty()) {
	        return ResponseEntity.badRequest().body(macdData);
	    }

	    return ResponseEntity.ok(macdData);
	}

	@GetMapping("/{id}/indicadores")
	public ResponseEntity<Map<String, Object>> getIndicadores(@PathVariable String id) {
	    List<List<Double>> historicalPrices = criptomonedaService.getHistoricalPrices(id);

	    if (historicalPrices.isEmpty()) {
	        return ResponseEntity.badRequest().body(Map.of("error", "No hay datos históricos"));
	    }

	    List<Double> precios = historicalPrices.stream()
	        .map(pair -> pair.get(1))
	        .collect(Collectors.toList());

	    Map<String, Object> indicadores = new HashMap<>();

	    indicadores.put("precios", precios);

	    if (precios.size() >= 15) {
	        List<Double> rsi = RSIUtil.calculateRSIList(precios, 14);
	        indicadores.put("rsi", rsi);
	    } else {
	        indicadores.put("rsi", List.of());
	    }

	    if (precios.size() >= 7) {
	        List<Double> sma = SMAUtil.calcularSMA(precios, 7);
	        indicadores.put("sma", sma);
	    } else {
	        indicadores.put("sma", List.of());
	    }

	    if (precios.size() >= 7) {
	        List<Double> ema = EMAUtil.calcularEMA(precios, 7);
	        indicadores.put("ema", ema);
	    } else {
	        indicadores.put("ema", List.of());
	    }

	    if (precios.size() >= 35) {
	        Map<String, List<Double>> macd = MACDUtil.calcularMACD(precios);
	        indicadores.put("macd", macd.getOrDefault("macd", List.of()));
	        indicadores.put("signal", macd.getOrDefault("signal", List.of()));
	    } else {
	        indicadores.put("macd", List.of());
	        indicadores.put("signal", List.of());
	    }

	    return ResponseEntity.ok(indicadores);
	}

	@GetMapping("/{id}/rsi/history")
	public ResponseEntity<List<Double>> getRsiHistory(@PathVariable String id) {
		List<Double> precios = criptomonedaService.getHistoricalPrices(id)
                .stream()
                .map(pair -> pair.get(1))
                .collect(Collectors.toList());
	    List<Double> rsi = RSIUtil.calculateRSIList(precios, 14);
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
	
	@PostMapping("/buy")
	public ResponseEntity<?> comprarCrypto(@RequestBody CompraRequestDTO compra) {
	    System.out.println("Entrando a /buy");
	    System.out.println("usuarioId: " + compra.getUsuarioId());
	    System.out.println("cantidad: " + compra.getCantidadCrypto() + ", precio: " + compra.getPrecio());

	    try {
	        Transaccion transaccion = transaccionService.comprarCrypto(
	            compra.getUsuarioId(),
	            compra.getSimbolo(),
	            compra.getNombreCrypto(),
	            compra.getCantidadCrypto(),
	            compra.getPrecio()
	        );
	        
	        if (transaccion == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
	                Map.of("estado", "error", "mensaje", "No se pudo realizar la transacción.")
	            );
	        }
	        
	        System.out.println("Compra realizada. Total: " + transaccion.getValorTotal());

	        Map<String, Object> respuesta = Map.of(
	            "estado", "exito",
	            "mensaje", "Compra realizada con éxito",
	            "detalle", Map.of(
	                "simbolo", transaccion.getCryptoId(),
	                "cantidad", transaccion.getCantidadCrypto(),
	                "precio", transaccion.getPrecioTransaccion(),
	                "valorTotal", transaccion.getValorTotal()
	            )
	        );

	        return ResponseEntity.ok(respuesta);

	    } catch (RuntimeException e) {
	        System.out.println("Error en compra: " + e.getMessage());

	        Map<String, Object> respuesta = Map.of(
	            "estado", "error",
	            "mensaje", "Ocurrió un error al procesar la compra",
	            "detalle", e.getMessage()
	        );

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
	    }
	}

	@PostMapping("/sell")
	public ResponseEntity<?> venderCrypto(@RequestBody VentaRequestDTO venta) {
	    System.out.println("Entrando a /sell");
	    System.out.println("usuarioId: " + venta.getUsuarioId());
	    System.out.println("cantidad: " + venta.getCantidadCrypto() + ", precio: " + venta.getPrecio());

	    try {
	        Transaccion transaccion = transaccionService.venderCrypto(
	            venta.getUsuarioId(),
	            venta.getSimbolo(),
	            venta.getNombreCrypto(),
	            venta.getCantidadCrypto(),
	            venta.getPrecio()
	        );

	        if (transaccion == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
	                Map.of("estado", "error", "mensaje", "No se pudo realizar la venta.")
	            );
	        }

	        System.out.println("Venta realizada. Total: " + transaccion.getValorTotal());

	        Map<String, Object> respuesta = Map.of(
	            "estado", "exito",
	            "mensaje", "Venta realizada con éxito",
	            "detalle", Map.of(
	                "simbolo",    transaccion.getCryptoId(),
	                "cantidad",   transaccion.getCantidadCrypto(),
	                "precio",     transaccion.getPrecioTransaccion(),
	                "valorTotal", transaccion.getValorTotal()
	            )
	        );

	        return ResponseEntity.ok(respuesta);

	    } catch (RuntimeException e) {
	        System.out.println("Error en venta: " + e.getMessage());

	        Map<String, Object> respuesta = Map.of(
	            "estado", "error",
	            "mensaje", "Ocurrió un error al procesar la venta",
	            "detalle", e.getMessage()
	        );

	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
	    }
	}
}

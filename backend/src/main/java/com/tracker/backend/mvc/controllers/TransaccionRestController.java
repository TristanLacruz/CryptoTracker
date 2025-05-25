package com.tracker.backend.mvc.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.tracker.common.dto.CompraRequestDTO;
import com.tracker.common.dto.CriptoPosesionDTO;
import com.tracker.common.dto.ValorDiarioDTO;
import com.tracker.backend.mvc.model.entity.Transaccion;
import com.tracker.backend.mvc.model.entity.TransactionType;
import com.tracker.backend.mvc.model.services.ITransaccionService;
import com.tracker.backend.mvc.model.services.ICriptomonedaService;
import com.tracker.backend.mvc.model.services.IPortafolioService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/transacciones")
public class TransaccionRestController {

	@Autowired
	private ITransaccionService transaccionService;

	@Autowired
	private IPortafolioService portafolioService;

	@Autowired
	private ICriptomonedaService cryptoService;

	@GetMapping
	public List<Transaccion> getTransaccion() {
		return transaccionService.findAll();
	}

	@PostMapping("/transaccion")
	@ResponseStatus(HttpStatus.CREATED)
	public Transaccion createTransaccion(@RequestBody Transaccion Transaccion) {
		transaccionService.save(Transaccion);
		return Transaccion;
	}

	@PostMapping("/comprar")
	public Transaccion comprarCrypto(@RequestBody CompraRequestDTO dto) {
		System.out.println("Datos recibidos en compra:");
		System.out.println("usuarioId: " + dto.getUsuarioId());
		System.out.println("simbolo: " + dto.getSimbolo());
		System.out.println("nombreCrypto: " + dto.getNombreCrypto());
		System.out.println("cantidadCrypto: " + dto.getCantidadCrypto());
		System.out.println("precio: " + dto.getPrecio());

		if (dto.getCantidadCrypto() <= 0 || dto.getPrecio() <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cantidad o precio inválido");
		}

		return transaccionService.comprarCrypto(
				dto.getUsuarioId(),
				dto.getSimbolo(),
				dto.getNombreCrypto(),
				dto.getCantidadCrypto(),
				dto.getPrecio());
	}

	@GetMapping("/usuario")
	public List<Transaccion> getByUsuario(@RequestParam String usuarioId) {
		return transaccionService.findByUsuarioId(usuarioId);
	}

	@GetMapping("/mis-transacciones")
	public List<Transaccion> getMisTransacciones(Authentication authentication) {
		String uid = (String) authentication.getPrincipal();
		return transaccionService.findByUsuarioId(uid);
	}

	@GetMapping("/invertido/{usuarioId}")
	public ResponseEntity<Double> getTotalInvertido(@PathVariable String usuarioId) {
		double total = transaccionService.getTotalInvertido(usuarioId);
		return ResponseEntity.ok(total);
	}

	@PostMapping("/test/actualizar-portafolio")
	public ResponseEntity<?> testActualizarPortafolio(@RequestBody Transaccion transaccion) {
		try {
			portafolioService.actualizarPortafolioConTransaccion(transaccion);
			return ResponseEntity.ok(Map.of("estado", "ok", "mensaje", "Portafolio actualizado"));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(Map.of("estado", "error", "mensaje", e.getMessage()));
		}
	}

	@PostMapping("/api/transacciones/test/actualizar-portafolio")
	public ResponseEntity<?> actualizarPortafolio(@RequestBody Transaccion transaccion) {
		portafolioService.actualizarPortafolioConTransaccion(transaccion);
		return ResponseEntity.ok(Map.of("estado", "ok"));
	}

	@GetMapping("/me/activos")
	public List<CriptoPosesionDTO> getMisActivos(Authentication auth) {
		String usuarioId = auth.getName();
		return getActivosActuales(usuarioId);
	}

	@GetMapping("/{usuarioId}/activos")
	public List<CriptoPosesionDTO> getActivosActuales(@PathVariable String usuarioId) {
		List<Transaccion> transacciones = transaccionService.findByUsuarioId(usuarioId);

		Map<String, Double> saldoPorCripto = new HashMap<>();
		for (Transaccion t : transacciones) {
			double cantidad = t.getCantidadCrypto();
			if (t.getTipoTransaccion() == TransactionType.VENDER) {
				cantidad *= -1;
			}
			saldoPorCripto.merge(t.getCryptoId(), cantidad, Double::sum);
		}

		List<CriptoPosesionDTO> activos = new ArrayList<>();
		for (Map.Entry<String, Double> entry : saldoPorCripto.entrySet()) {
			String simbolo = entry.getKey();
			double cantidad = entry.getValue();
			if (cantidad <= 0)
				continue;

			double precioActual = cryptoService.getPrecioActual(simbolo);
			double valorTotal = cantidad * precioActual;

			activos.add(new CriptoPosesionDTO(simbolo, cantidad, valorTotal));
		}

		return activos;
	}

	@GetMapping("/{usuarioId}/evolucion")
	public List<ValorDiarioDTO> getEvolucionDesdeTransacciones(@PathVariable String usuarioId) {
		List<Transaccion> transacciones = transaccionService.findByUsuarioId(usuarioId);
		TreeMap<LocalDate, Double> saldoPorDia = new TreeMap<>();

		double saldoAcumulado = 0;

		transacciones.sort(Comparator.comparing(Transaccion::getFechaTransaccion));

		for (Transaccion t : transacciones) {
			LocalDate fecha = t.getFechaTransaccion().toLocalDate();
			double valor = t.getValorTotal();
			if (t.getTipoTransaccion() == TransactionType.VENDER) {
				saldoAcumulado += valor;
			} else {
				saldoAcumulado -= valor;
			}
			saldoPorDia.put(fecha, saldoAcumulado);
		}

		List<ValorDiarioDTO> resultado = new ArrayList<>();
		for (Map.Entry<LocalDate, Double> entry : saldoPorDia.entrySet()) {
			resultado.add(new ValorDiarioDTO(entry.getKey(), entry.getValue()));
		}

		return resultado;
	}

}

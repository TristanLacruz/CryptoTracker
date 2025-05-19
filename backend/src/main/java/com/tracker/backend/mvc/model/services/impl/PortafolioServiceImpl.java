package com.tracker.backend.mvc.model.services.impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracker.backend.mvc.model.dao.IPortafolioDAO;
import com.tracker.backend.mvc.model.dao.ITransaccionDAO;
import com.tracker.common.dto.EvolucionCompletaDTO;
import com.tracker.common.dto.RendimientoDiarioDTO;
import com.tracker.common.dto.ValorDiarioDTO;
import com.tracker.backend.mvc.model.entity.Portafolio;
import com.tracker.backend.mvc.model.entity.Transaccion;
import com.tracker.backend.mvc.model.entity.TransactionType;
import com.tracker.backend.mvc.model.exceptions.PortafolioNoEncontradoException;
import com.tracker.backend.mvc.model.services.IPortafolioService;
import com.tracker.backend.mvc.model.services.ICriptomonedaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PortafolioServiceImpl implements IPortafolioService {
	private final IPortafolioDAO portafolioDAO;
	private final ITransaccionDAO transaccionDAO;
	private final ICriptomonedaService cryptoService;
    
    private static final Logger log = LoggerFactory.getLogger(PortafolioServiceImpl.class);
    
	@Autowired
	public PortafolioServiceImpl(IPortafolioDAO portafolioDAO, ITransaccionDAO transaccionDAO,
			ICriptomonedaService cryptoService) {
		this.portafolioDAO = portafolioDAO;
		this.transaccionDAO = transaccionDAO;
		this.cryptoService = cryptoService;
	}

	// ... resto de métodos ...

	@Override
	public List<Portafolio> findAll() {
		return (List<Portafolio>) portafolioDAO.findAll();
	}

	@Override
	public void save(Portafolio p) {
		portafolioDAO.save(p);
	}

	@Override
	public Portafolio findById(String id) {
		return portafolioDAO.findById(id).orElseThrow(() -> new PortafolioNoEncontradoException(id));
	}

	@Override
	public void delete(Portafolio p) {
		portafolioDAO.delete(p);
	}

	@Override
	public Portafolio update(Portafolio p, String id) {
		Portafolio portafolioActual = this.findById(id);
		portafolioActual.setUsuarioId(p.getUsuarioId());
		portafolioActual.setCriptomonedas(p.getCriptomonedas());
		return portafolioActual;
	}

	@Override
	public void anadirCrypto(String usuarioId, String simbolo, double quantity) {
		Portafolio Portafolio = portafolioDAO.findByUsuarioId(usuarioId).orElse(new Portafolio(usuarioId));

		Portafolio.getCriptomonedas().merge(simbolo, quantity, Double::sum);
		portafolioDAO.save(Portafolio);
	}

	@Override
	public void eliminarCrypto(String usuarioId, String simbolo, double quantity) {
		Portafolio Portafolio = portafolioDAO.findByUsuarioId(usuarioId)
				.orElseThrow(() -> new RuntimeException("Portafolio no encontrado"));

		Map<String, Double> cryptos = Portafolio.getCriptomonedas();
		double currentAmount = cryptos.getOrDefault(simbolo, 0.0);

		if (currentAmount < quantity) {
			throw new RuntimeException("No tienes suficiente cantidad de " + simbolo);
		}

		double newAmount = currentAmount - quantity;

		if (newAmount == 0) {
			cryptos.remove(simbolo);
		} else {
			cryptos.put(simbolo, newAmount);
		}

		portafolioDAO.save(Portafolio);
	}

	@Override
	public boolean tieneSuficienteCrypto(String usuarioId, String simbolo, double quantity) {
		Portafolio Portafolio = portafolioDAO.findByUsuarioId(usuarioId).orElse(new Portafolio(usuarioId));

		return Portafolio.getCriptomonedas().getOrDefault(simbolo, 0.0) >= quantity;
	}

	@Override
	public Portafolio getPortafolioDeUsuarioId(String usuarioId) {
	    Portafolio p = portafolioDAO.findByUsuarioId(usuarioId)
	        .orElseGet(() -> {
	            Portafolio nuevo = new Portafolio(usuarioId);
	            nuevo.setSaldo(10000);
	            nuevo.setCriptomonedas(new HashMap<>());
	            return portafolioDAO.save(nuevo);
	        });
	    log.debug("Portafolio para {} = {}", usuarioId, p);	    return p;
	}


	@Override
	public void updatePortafolioDespuesDeCompra(String usuarioId, String cryptoId, double cantidadCrypto) {
		Portafolio Portafolio = portafolioDAO.findById(usuarioId).orElseGet(() -> {
			Portafolio nuevoPortafolio = new Portafolio();
			nuevoPortafolio.setId(usuarioId);
			nuevoPortafolio.setCriptomonedas(null);
			return nuevoPortafolio;
		});

		Map<String, Double> criptomonedas = Portafolio.getCriptomonedas();

		// Sumar la cantidad nueva a la ya existente (si existe)
		double cantidadActual = criptomonedas.getOrDefault(cryptoId, 0.0);
		criptomonedas.put(cryptoId, cantidadActual + cantidadCrypto);

		Portafolio.setCriptomonedas(criptomonedas);
		portafolioDAO.save(Portafolio);
	}

	public List<ValorDiarioDTO> calcularEvolucion(String usuarioId) {
		List<Transaccion> transacciones = transaccionDAO.findByUsuarioIdOrderByFechaTransaccionAsc(usuarioId);

		if (transacciones.isEmpty())
			return List.of();

		LocalDate inicio = transacciones.get(0).getFechaTransaccion().toLocalDate();
		LocalDate hoy = LocalDate.now();
		long dias = ChronoUnit.DAYS.between(inicio, hoy);

		Map<String, Double> cantidades = new HashMap<>();
		List<ValorDiarioDTO> evolucion = new ArrayList<>();

		for (int i = 0; i <= dias; i++) {
			LocalDate fecha = inicio.plusDays(i);

			// Aplicar transacciones de ese día
			for (Transaccion tx : transacciones) {
				if (tx.getFechaTransaccion().toLocalDate().equals(fecha)) {
					cantidades.putIfAbsent(tx.getCryptoId(), 0.0);
					double cantidadActual = cantidades.get(tx.getCryptoId());
					double cantidadNueva = tx.getTipoTransaccion() == TransactionType.COMPRAR
							? cantidadActual + tx.getCantidadCrypto()
							: cantidadActual - tx.getCantidadCrypto();
					cantidades.put(tx.getCryptoId(), cantidadNueva);
				}
			}

			// Calcular valor total del portafolio ese día
			double valorDia = 0;
			for (Map.Entry<String, Double> entry : cantidades.entrySet()) {
				String criptoId = entry.getKey();
				double cantidad = entry.getValue();

				if (cantidad <= 0)
					continue;

				// Reemplaza por tu servicio de precios históricos
				double precio = cryptoService.getPrecioEnFecha(criptoId, fecha);
				valorDia += precio * cantidad;
			}

			evolucion.add(new ValorDiarioDTO(i, valorDia));
		}

		return evolucion;
	}

	public List<RendimientoDiarioDTO> calcularRendimiento(String usuarioId) {
		List<Transaccion> transacciones = transaccionDAO.findByUsuarioIdOrderByFechaTransaccionAsc(usuarioId);

		if (transacciones.isEmpty())
			return List.of();

		LocalDate inicio = transacciones.get(0).getFechaTransaccion().toLocalDate();
		LocalDate hoy = LocalDate.now();
		long dias = ChronoUnit.DAYS.between(inicio, hoy);

		Map<String, Double> cantidades = new HashMap<>();
		double inversionAcumulada = 0;
		List<RendimientoDiarioDTO> lista = new ArrayList<>();

		for (int i = 0; i <= dias; i++) {
			LocalDate fecha = inicio.plusDays(i);

			// Aplicar transacciones del día
			for (Transaccion tx : transacciones) {
				if (tx.getFechaTransaccion().toLocalDate().equals(fecha)) {
					double cantidad = cantidades.getOrDefault(tx.getCryptoId(), 0.0);

					if (tx.getTipoTransaccion() == TransactionType.COMPRAR) {
						inversionAcumulada += tx.getValorTotal();
						cantidad += tx.getCantidadCrypto();
					} else {
						cantidad -= tx.getCantidadCrypto();
					}

					cantidades.put(tx.getCryptoId(), cantidad);
				}
			}

			// Calcular valor actual del portafolio ese día
			double valorDia = 0;
			for (Map.Entry<String, Double> entry : cantidades.entrySet()) {
				double cantidad = entry.getValue();
				if (cantidad <= 0)
					continue;

				double precio = cryptoService.getPrecioEnFecha(entry.getKey(), fecha);
				valorDia += cantidad * precio;
			}

			double ganancia = valorDia - inversionAcumulada;
			lista.add(new RendimientoDiarioDTO(i, ganancia));
		}

		return lista;
	}

	@Override
	public List<EvolucionCompletaDTO> calcularEvolucionCompleta(String usuarioId) {
		List<Transaccion> transacciones = transaccionDAO.findByUsuarioIdOrderByFechaTransaccionAsc(usuarioId);

		if (transacciones.isEmpty())
			return List.of();

		LocalDate inicio = transacciones.get(0).getFechaTransaccion().toLocalDate();
		LocalDate hoy = LocalDate.now();
		long dias = ChronoUnit.DAYS.between(inicio, hoy);

		Map<String, Double> cantidades = new HashMap<>();
		double inversionAcumulada = 0;

		List<EvolucionCompletaDTO> lista = new ArrayList<>();

		for (int i = 0; i <= dias; i++) {
			LocalDate fecha = inicio.plusDays(i);

			for (Transaccion tx : transacciones) {
				if (tx.getFechaTransaccion().toLocalDate().equals(fecha)) {
					double actual = cantidades.getOrDefault(tx.getCryptoId(), 0.0);
					if (tx.getTipoTransaccion() == TransactionType.COMPRAR) {
						inversionAcumulada += tx.getValorTotal();
						actual += tx.getCantidadCrypto();
					} else {
						actual -= tx.getCantidadCrypto();
					}
					cantidades.put(tx.getCryptoId(), actual);
				}
			}

			double valorDia = 0;
			for (Map.Entry<String, Double> entry : cantidades.entrySet()) {
				if (entry.getValue() <= 0)
					continue;
				double precio = cryptoService.getPrecioEnFecha(entry.getKey(), fecha);
				valorDia += entry.getValue() * precio;
			}

			double ganancia = valorDia - inversionAcumulada;
			lista.add(new EvolucionCompletaDTO(i, valorDia, ganancia));
		}

		return lista;
	}

	@Override
	public Portafolio findByUsuarioId(String usuarioId) {
		return portafolioDAO.findByUsuarioId(usuarioId)
				.orElseThrow(() -> new RuntimeException("Portafolio no encontrado para el usuario: " + usuarioId));
	}

}

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

/**
 * Implementación del servicio de portafolio que maneja las operaciones relacionadas
 * con los portafolios de criptomonedas de los usuarios.
 */
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

	/*
	 * Método que devuelve una lista de todos los portafolios.
	 * @return Lista de portafolios.
	 */
	@Override
	public List<Portafolio> findAll() {
		return (List<Portafolio>) portafolioDAO.findAll();
	}

	/*
	 * Método que guarda un portafolio en la base de datos.
	 * @param p Portafolio a guardar.
	 */
	@Override
	public void save(Portafolio p) {
		portafolioDAO.save(p);
	}

	/*
	 * Método que busca un portafolio por su ID.
	 * @param id ID del portafolio a buscar.
	 * @return Portafolio encontrado.
	 * @throws PortafolioNoEncontradoException Si no se encuentra el portafolio.
	 */
	@Override
	public Portafolio findById(String id) {
		return portafolioDAO.findById(id).orElseThrow(() -> new PortafolioNoEncontradoException(id));
	}

	/*
	 * Método que elimina un portafolio de la base de datos.
	 * @param p Portafolio a eliminar.
	 */
	@Override
	public void delete(Portafolio p) {
		portafolioDAO.delete(p);
	}

	/*
	 * Método que actualiza un portafolio.
	 * @param p Portafolio a actualizar.
	 * @param id ID del portafolio a actualizar.
	 * @return Portafolio actualizado.
	 */
	@Override
	public Portafolio update(Portafolio p, String id) {
		Portafolio portafolioActual = this.findById(id);
		portafolioActual.setUsuarioId(p.getUsuarioId());
		portafolioActual.setCriptomonedas(p.getCriptomonedas());
		return portafolioActual;
	}

	/*
	 * Método que busca un portafolio por el ID de usuario.
	 * @param usuarioId ID del usuario.
	 * @return Portafolio encontrado.
	 */
	@Override
	public void anadirCrypto(String usuarioId, String simbolo, double quantity) {
		Portafolio Portafolio = portafolioDAO.findByUsuarioId(usuarioId).orElse(new Portafolio(usuarioId));
		System.out.println("Buscando portafolio por userId = " + usuarioId + " y criptoId = " + simbolo);

		Portafolio.getCriptomonedas().merge(simbolo, quantity, Double::sum);
		portafolioDAO.save(Portafolio);
	}

	/*
	 * Método que elimina una criptomoneda del portafolio de un usuario.
	 * @param usuarioId ID del usuario.
	 * @param simbolo Símbolo de la criptomoneda a eliminar.
	 * @param quantity Cantidad de criptomonedas a eliminar.
	 */
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

	/*
	 * Método que verifica si un usuario tiene suficiente cantidad de una criptomoneda.
	 * @param usuarioId ID del usuario.
	 * @param simbolo Símbolo de la criptomoneda.
	 * @param quantity Cantidad a verificar.
	 * @return true si tiene suficiente, false en caso contrario.
	 */
	@Override
	public boolean tieneSuficienteCrypto(String usuarioId, String simbolo, double quantity) {
		Portafolio Portafolio = portafolioDAO.findByUsuarioId(usuarioId).orElse(new Portafolio(usuarioId));

		return Portafolio.getCriptomonedas().getOrDefault(simbolo, 0.0) >= quantity;
	}

	/*
	 * Método que obtiene el portafolio de un usuario por su ID.
	 * Si no existe, crea uno nuevo con saldo inicial de 10.000 €.
	 * @param usuarioId ID del usuario.
	 * @return Portafolio del usuario.
	 */
	@Override
	public Portafolio getPortafolioDeUsuarioId(String usuarioId) {
		return portafolioDAO.findByUsuarioId(usuarioId).orElseGet(() -> {
			Portafolio nuevo = new Portafolio();
			nuevo.setUsuarioId(usuarioId);
			nuevo.setSaldo(10000.0);
			nuevo.setCriptomonedas(new HashMap<>());
			portafolioDAO.save(nuevo);
			System.out.println("📦 Nuevo portafolio creado para UID: " + usuarioId + " con saldo inicial de 10.000 €");
			return nuevo;
		});
	}

	/*
	 * Método que actualiza el portafolio después de una compra.
	 * @param usuarioId ID del usuario.
	 * @param cryptoId ID de la criptomoneda comprada.
	 * @param cantidadCrypto Cantidad de criptomonedas compradas.
	 */
	@Override
	public void updatePortafolioDespuesDeCompra(String usuarioId, String cryptoId, double cantidadCrypto) {
		Portafolio Portafolio = portafolioDAO.findById(usuarioId).orElseGet(() -> {
			Portafolio nuevoPortafolio = new Portafolio();
			nuevoPortafolio.setId(usuarioId);
			nuevoPortafolio.setCriptomonedas(new HashMap<>());
			return nuevoPortafolio;
		});

		Map<String, Double> criptomonedas = Portafolio.getCriptomonedas();

		double cantidadActual = criptomonedas.getOrDefault(cryptoId, 0.0);
		criptomonedas.put(cryptoId, cantidadActual + cantidadCrypto);

		Portafolio.setCriptomonedas(criptomonedas);
		portafolioDAO.save(Portafolio);
	}

	/*
	 * Método que actualiza el portafolio después de una venta.
	 * @param usuarioId ID del usuario.
	 * @param cryptoId ID de la criptomoneda vendida.
	 * @param cantidadCrypto Cantidad de criptomonedas vendidas.
	 */
	@Override
	public List<ValorDiarioDTO> calcularEvolucion(String usuarioId) {
		List<Transaccion> transacciones = transaccionDAO.findByUsuarioIdOrderByFechaTransaccionAsc(usuarioId);

		if (transacciones.isEmpty())
			return List.of();

		LocalDate inicio = transacciones.get(0).getFechaTransaccion().toLocalDate();
		LocalDate hoy = LocalDate.now();
		long dias = ChronoUnit.DAYS.between(inicio, hoy);

		if (dias < 6) {
			inicio = hoy.minusDays(6);
			dias = 6;
		}

		Map<String, Double> cantidades = new HashMap<>();
		List<ValorDiarioDTO> evolucion = new ArrayList<>();
		double saldo = 10000.0;

		for (int i = 0; i <= dias; i++) {
			LocalDate fecha = inicio.plusDays(i);

			for (Transaccion tx : transacciones) {
				if (tx.getFechaTransaccion().toLocalDate().isEqual(fecha)) {
					cantidades.putIfAbsent(tx.getCryptoId(), 0.0);
					double actual = cantidades.get(tx.getCryptoId());

					if (tx.getTipoTransaccion() == TransactionType.COMPRAR) {
						cantidades.put(tx.getCryptoId(), actual + tx.getCantidadCrypto());
						saldo -= tx.getValorTotal();
					} else {
						cantidades.put(tx.getCryptoId(), actual - tx.getCantidadCrypto());
						saldo += tx.getValorTotal();
					}
				}
			}

			double valorCripto = 0;
			for (Map.Entry<String, Double> entry : cantidades.entrySet()) {
				if (entry.getValue() <= 0)
					continue;

				double precio = cryptoService.getPrecioEnFecha(entry.getKey(), fecha);
				if (precio <= 0)
					precio = cryptoService.getPrecioActual(entry.getKey());

				valorCripto += entry.getValue() * precio;
			}

			double total = saldo + valorCripto;
			evolucion.add(new ValorDiarioDTO(fecha, total));
		}

		return evolucion;
	}

	/*
	 * Método que actualiza el portafolio después de una compra.
	 * @param uid ID del usuario.
	 * @param cryptoId ID de la criptomoneda comprada.
	 * @param cantidad Cantidad de criptomonedas compradas.
	 * @param precioCompra Precio de compra de la criptomoneda.
	 */
	@Override
	public void actualizarPortafolio(String uid, String cryptoId, double cantidad, double precioCompra) {
		Portafolio portafolio = portafolioDAO.findByUsuarioId(uid).orElseGet(() -> {
			Portafolio nuevo = new Portafolio();
			nuevo.setUsuarioId(uid);
			nuevo.setCriptomonedas(new HashMap<>());
			nuevo.setSaldo(10000);
			return nuevo;
		});

		Map<String, Double> criptos = portafolio.getCriptomonedas();
		criptos.put(cryptoId, criptos.getOrDefault(cryptoId, 0.0) + cantidad);
		portafolio.setCriptomonedas(criptos);

		portafolioDAO.save(portafolio);
	}

	/*
	 * Método que actualiza el portafolio después de una venta.
	 * @param uid ID del usuario.
	 * @param cryptoId ID de la criptomoneda vendida.
	 * @param cantidad Cantidad de criptomonedas vendidas.
	 * @param precioVenta Precio de venta de la criptomoneda.
	 */
	@Override
	public List<RendimientoDiarioDTO> calcularRendimiento(String usuarioId) {
		List<Transaccion> transacciones = transaccionDAO.findByUsuarioIdOrderByFechaTransaccionAsc(usuarioId);

		if (transacciones.isEmpty())
			return List.of();

		LocalDate inicio = transacciones.get(0).getFechaTransaccion().toLocalDate();
		LocalDate hoy = LocalDate.now();
		long dias = ChronoUnit.DAYS.between(inicio, hoy);

		if (dias < 6) {
			inicio = hoy.minusDays(6);
			dias = 6;
		}

		Map<String, Double> cantidades = new HashMap<>();
		double inversionAcumulada = 0;
		List<RendimientoDiarioDTO> lista = new ArrayList<>();

		for (int i = 0; i <= dias; i++) {
			LocalDate fecha = inicio.plusDays(i);
			System.out.printf("Día %d (%s)\n", i, fecha);

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

			double valorDia = 0;
			for (Map.Entry<String, Double> entry : cantidades.entrySet()) {
				String criptoId = entry.getKey();
				double cantidad = entry.getValue();
				if (cantidad <= 0)
					continue;

				double precio = cryptoService.getPrecioEnFecha(criptoId, fecha);
				if (precio <= 0) {
					System.out.printf("Precio histórico no disponible para %s en %s. Usando precio actual.%n", criptoId,
							fecha);
					precio = cryptoService.getPrecioActual(criptoId);
				}

				double subtotal = cantidad * precio;
				System.out.printf("%s: %.4f × %.4f€ = %.2f€\n", criptoId, cantidad, precio, subtotal);
				valorDia += subtotal;
			}

			double ganancia = valorDia - inversionAcumulada;
			System.out.printf("Inversión acumulada: %.2f€, Valor: %.2f€, Ganancia: %.2f€\n\n", inversionAcumulada,
					valorDia, ganancia);
			lista.add(new RendimientoDiarioDTO(i, ganancia));
		}

		return lista;
	}

	/**
	 * Método que calcula la evolución completa del portafolio de un usuario.
	 * @param usuarioId ID del usuario.
	 * @return Lista de EvolucionCompletaDTO con la evolución diaria del portafolio.
	 */
	@Override
	public List<EvolucionCompletaDTO> calcularEvolucionCompleta(String usuarioId) {
		List<Transaccion> transacciones = transaccionDAO.findByUsuarioIdOrderByFechaTransaccionAsc(usuarioId);
		if (transacciones.isEmpty())
			return List.of();

		Map<String, Double> cantidades = new HashMap<>();
		List<EvolucionCompletaDTO> evolucion = new ArrayList<>();
		double saldoDisponible = 10000.0;

		LocalDate inicio = transacciones.get(0).getFechaTransaccion().toLocalDate();
		LocalDate fin = LocalDate.now();

		for (LocalDate fecha = inicio; !fecha.isAfter(fin); fecha = fecha.plusDays(1)) {

			for (Transaccion tx : transacciones) {
				if (tx.getFechaTransaccion().toLocalDate().equals(fecha)) {
					String cripto = tx.getCryptoId();
					double cantidad = tx.getCantidadCrypto();
					double valor = tx.getValorTotal();

					cantidades.putIfAbsent(cripto, 0.0);
					if (tx.getTipoTransaccion() == TransactionType.COMPRAR) {
						cantidades.put(cripto, cantidades.get(cripto) + cantidad);
						saldoDisponible -= valor;
					} else if (tx.getTipoTransaccion() == TransactionType.VENDER) {
						cantidades.put(cripto, cantidades.get(cripto) - cantidad);
						saldoDisponible += valor;
					}
				}
			}

			double valorCriptos = 0;
			for (Map.Entry<String, Double> entry : cantidades.entrySet()) {
				double cantidad = entry.getValue();
				if (cantidad <= 0)
					continue;

				double precio;
				try {
					precio = cryptoService.getPrecioEnFecha(entry.getKey(), fecha);
					if (precio <= 0)
						throw new RuntimeException("Precio no válido");
				} catch (Exception e) {
					System.err.println("Precio histórico no disponible para " + entry.getKey() + " el " + fecha
							+ ". Usando precio actual.");
					precio = cryptoService.getPrecioActual(entry.getKey());
				}

				double subtotal = cantidad * precio;

				System.out.printf("[%s] Cantidad: %.6f | Precio: %.2f | Subtotal: %.2f\n", entry.getKey(), cantidad,
						precio, subtotal);

				valorCriptos += subtotal;

			}

			double total = saldoDisponible + valorCriptos;
			int dia = (int) ChronoUnit.DAYS.between(inicio, fecha);
			evolucion.add(new EvolucionCompletaDTO(dia, total, saldoDisponible, valorCriptos));
		}

		if (!fin.isEqual(LocalDate.now())) {
			double saldoActual = this.obtenerSaldo(usuarioId);
			double valorActual = cryptoService.calcularValorActualEnCriptos(usuarioId);
			double balanceActual = saldoActual + valorActual;
			int diaActual = (int) ChronoUnit.DAYS.between(inicio, LocalDate.now());

			evolucion.add(new EvolucionCompletaDTO(diaActual, balanceActual, saldoActual, valorActual));
		}

		return evolucion;
	}

	/**
	 * Método que actualiza el portafolio de un usuario con una transacción.
	 * @param transaccion Transacción a procesar.
	 */
	@Override
	public void actualizarPortafolioConTransaccion(Transaccion transaccion) {
		String usuarioId = transaccion.getUsuarioId();
		String cryptoId = transaccion.getCryptoId();
		double cantidad = transaccion.getCantidadCrypto();
		boolean esCompra = transaccion.getTipoTransaccion() == TransactionType.COMPRAR;

		Portafolio portafolio = getPortafolioDeUsuarioId(usuarioId); 

		Map<String, Double> criptos = portafolio.getCriptomonedas();
		double cantidadActual = criptos.getOrDefault(cryptoId, 0.0);
		double nuevaCantidad = esCompra ? cantidadActual + cantidad : cantidadActual - cantidad;

		if (nuevaCantidad <= 0) {
			criptos.remove(cryptoId);
		} else {
			criptos.put(cryptoId, nuevaCantidad);
		}
		portafolioDAO.save(portafolio);
	}

	/**
	 * Método que busca un portafolio por el ID de usuario.
	 * @param usuarioId ID del usuario.
	 * @return Portafolio encontrado.
	 */
	@Override
	public Portafolio findByUsuarioId(String usuarioId) {
		return portafolioDAO.findByUsuarioId(usuarioId)
				.orElseThrow(() -> new RuntimeException("Portafolio no encontrado para el usuario: " + usuarioId));
	}

	/**
	 * Método que obtiene el saldo del portafolio de un usuario.
	 * @param usuarioId ID del usuario.
	 * @return Saldo del portafolio.
	 */
	@Override
	public double obtenerSaldo(String usuarioId) {
		return getPortafolioDeUsuarioId(usuarioId).getSaldo();
	}

}

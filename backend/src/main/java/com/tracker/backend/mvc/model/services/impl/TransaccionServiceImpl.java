package com.tracker.backend.mvc.model.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tracker.backend.mvc.model.dao.ITransaccionDAO;
import com.tracker.backend.mvc.model.entity.Portafolio;
import com.tracker.backend.mvc.model.entity.Transaccion;
import com.tracker.backend.mvc.model.entity.TransactionType;
import com.tracker.backend.mvc.model.exceptions.TransaccionNoEncontradaException;
import com.tracker.backend.mvc.model.services.IPortafolioService;
import com.tracker.backend.mvc.model.services.ITransaccionService;

/**
 * Implementación del servicio de transacciones.
 * Proporciona métodos para manejar transacciones de compra y venta de criptomonedas.
 */
@Service
public class TransaccionServiceImpl implements ITransaccionService {

	@Autowired
	private ITransaccionDAO transaccionDAO;

	@Autowired
	private IPortafolioService portafolioService;;

	/*
	 * Método para obtener todas las transacciones.
	 */
	@Override
	public List<Transaccion> findAll() {
		return (List<Transaccion>) transaccionDAO.findAll();
	}

	/*
	 * Método para guardar una transacción
	 */
	@Override
	public void save(Transaccion t) {
		transaccionDAO.save(t);
	}

	/*
	 * Método para buscar una transacción por su ID.
	 * Si no se encuentra, lanza una excepción TransaccionNoEncontradaException.
	 */
	@Override
	public Transaccion findById(String id) {
		return transaccionDAO.findById(id).orElseThrow(() -> new TransaccionNoEncontradaException(id));
	}

	/*
	 * Método para eliminar una transacción.
	 */
	@Override
	public void delete(Transaccion t) {
		transaccionDAO.delete(t);
	}

	/*
	 * Método para actualizar una transacción.
	 * Actualiza los campos de la transacción existente con los valores de la nueva transacción.
	 * @return currentTransaccion
	 */ 
	@Override
	public Transaccion update(Transaccion t, String id) {
		Transaccion currentTransaccion = this.findById(id);
		currentTransaccion.setUsuarioId(t.getUsuarioId());
		currentTransaccion.setTipoTransaccion(t.getTipoTransaccion());
		currentTransaccion.setFechaTransaccion(t.getFechaTransaccion());
		currentTransaccion.setValorTotal(t.getValorTotal());
		currentTransaccion.setPrecioTransaccion(t.getPrecioTransaccion());
		currentTransaccion.setCryptoId(t.getCryptoId());
		currentTransaccion.setCantidadCrypto(t.getCantidadCrypto());
		return currentTransaccion;
	}

	/*
	 * Método para comprar criptomonedas.
	 * Valida la cantidad y el precio, obtiene o crea el portafolio del usuario,
	 */
	@Override
	public Transaccion comprarCrypto(String usuarioId, String simbolo, String nombreCrypto, double cantidadCrypto,
			double precioUnitario) {

		if (cantidadCrypto <= 0 || precioUnitario <= 0) {
			throw new IllegalArgumentException("La cantidad y el precio deben ser mayores que cero.");
		}

		Portafolio portafolio = portafolioService.getPortafolioDeUsuarioId(usuarioId);

		if (portafolio == null) {
			System.out.println("Portafolio no encontrado. Creando nuevo...");
			portafolio = new Portafolio();
			portafolio.setUsuarioId(usuarioId);
			portafolio.setSaldo(10000.0); 
		}

		double totalCompra = cantidadCrypto * precioUnitario;

		System.out.println("UsuarioId del portafolio: " + portafolio.getUsuarioId());
		System.out.println("ID del portafolio: " + portafolio.getId());
		System.out.println("Saldo del portafolio: " + portafolio.getSaldo());
		System.out.println("Total compra: " + totalCompra);

		if (portafolio.getSaldo() < totalCompra) {
			throw new RuntimeException("Saldo insuficiente para realizar la compra.");
		}

		Transaccion transaccion = new Transaccion();
		transaccion.setUsuarioId(usuarioId);
		transaccion.setCryptoId(simbolo);
		transaccion.setTipoTransaccion(TransactionType.COMPRAR);
		transaccion.setCantidadCrypto(cantidadCrypto);
		transaccion.setPrecioTransaccion(precioUnitario);
		transaccion.setValorTotal(totalCompra);
		transaccion.setFechaTransaccion(LocalDateTime.now());

		System.out.println("Guardando transacción...");
		transaccionDAO.save(transaccion);
		System.out.println("Transacción guardada.");

		portafolio.agregarCripto(simbolo, cantidadCrypto);
		portafolio.setSaldo(portafolio.getSaldo() - totalCompra);
		System.out.println("Nuevo saldo: " + portafolio.getSaldo());

		portafolioService.save(portafolio);

		return transaccion;
	}

	/**
	 * Método para obtener el total invertido por un usuario.
	 * Suma el valor total de todas las transacciones de compra del usuario.
	 */
	@Override
	public double getTotalInvertido(String usuarioId) {
		List<Transaccion> compras = transaccionDAO.findByUsuarioIdAndTipoTransaccion(usuarioId,
				TransactionType.COMPRAR);
		return compras.stream()
				.mapToDouble(Transaccion::getValorTotal)
				.sum();
	}

	/*
	 * Método para obtener las transacciones de un usuario por su ID.
	 * Ordena las transacciones por fecha de transacción en orden descendente.
	 */
	@Override
	public List<Transaccion> findByUsuarioId(String usuarioId) {
		return transaccionDAO.findByUsuarioIdOrderByFechaTransaccionDesc(usuarioId);
	}

	/**
	 * Método para vender criptomonedas.
	 * Valida la cantidad y el precio, verifica si el usuario tiene suficiente criptomoneda,
	 * actualiza el portafolio y guarda la transacción.
	 */
	@Override
	public Transaccion venderCrypto(String usuarioId, String simbolo, String nombreCrypto, double cantidadCrypto,
			double precioUnitario) {

		if (cantidadCrypto <= 0 || precioUnitario <= 0) {
			throw new IllegalArgumentException("La cantidad y el precio deben ser mayores que cero.");
		}

		Portafolio portafolio = portafolioService.getPortafolioDeUsuarioId(usuarioId);
		if (portafolio == null) {
			throw new RuntimeException("No se encontró el portafolio del usuario.");
		}

		System.out.println("Portafolio obtenido para venta: " + portafolio.getId());
		System.out.println("Criptos antes de venta: " + portafolio.getCriptomonedas());

		if (!portafolioService.tieneSuficienteCrypto(usuarioId, simbolo, cantidadCrypto)) {
			throw new RuntimeException("No tienes suficiente " + simbolo + " para vender.");
		}

		double totalVenta = cantidadCrypto * precioUnitario;

		Transaccion transaccion = new Transaccion();
		transaccion.setUsuarioId(usuarioId);
		transaccion.setCryptoId(simbolo);
		transaccion.setTipoTransaccion(TransactionType.VENDER);
		transaccion.setCantidadCrypto(cantidadCrypto);
		transaccion.setPrecioTransaccion(precioUnitario);
		transaccion.setValorTotal(totalVenta);
		transaccion.setFechaTransaccion(LocalDateTime.now());

		transaccionDAO.save(transaccion);
		System.out.println("Transacción de venta guardada");

		Map<String, Double> cryptos = portafolio.getCriptomonedas();
		double actual = cryptos.getOrDefault(simbolo, 0.0);
		double restante = actual - cantidadCrypto;

		if (restante <= 0) {
			cryptos.remove(simbolo);
			System.out.println("Cripto eliminada del portafolio");
		} else {
			cryptos.put(simbolo, restante);
			System.out.println("Nueva cantidad de " + simbolo + ": " + restante);
		}

		double nuevoSaldo = portafolio.getSaldo() + totalVenta;
		portafolio.setSaldo(nuevoSaldo);
		System.out.println("Nuevo saldo del portafolio: " + nuevoSaldo);

		portafolioService.save(portafolio);
		System.out.println("Portafolio actualizado y guardado correctamente");

		return transaccion;
	}

}

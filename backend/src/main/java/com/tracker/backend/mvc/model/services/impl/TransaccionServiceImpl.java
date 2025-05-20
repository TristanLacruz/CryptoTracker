package com.tracker.backend.mvc.model.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

import com.tracker.backend.mvc.model.dao.IPortafolioDAO;
import com.tracker.backend.mvc.model.dao.ITransaccionDAO;
import com.tracker.backend.mvc.model.dao.IUsuarioDAO;
import com.tracker.backend.mvc.model.entity.Portafolio;
import com.tracker.backend.mvc.model.entity.Transaccion;
import com.tracker.backend.mvc.model.entity.TransactionType;
import com.tracker.backend.mvc.model.entity.Usuario;
import com.tracker.backend.mvc.model.exceptions.TransaccionNoEncontradaException;
import com.tracker.backend.mvc.model.exceptions.UsuarioNoEncontradoException;
import com.tracker.backend.mvc.model.services.ICriptomonedaService;
import com.tracker.backend.mvc.model.services.IPortafolioService;
import com.tracker.backend.mvc.model.services.ITransaccionService;
import com.tracker.backend.mvc.model.services.IUsuarioService;

@Service
public class TransaccionServiceImpl implements ITransaccionService {

	@Autowired
	private ITransaccionDAO transaccionDAO;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private ICriptomonedaService cryptoService;
	
	@Autowired
	private IPortafolioService portafolioService;

	@Autowired
	private IUsuarioDAO usuarioDAO;

	@Autowired
	private IPortafolioDAO portafolioDAO;
	
	@Autowired
	private ICriptomonedaService coingeckoService;

	@Override
	public List<Transaccion> findAll() {
		return (List<Transaccion>) transaccionDAO.findAll();
	}

	@Override
	public void save(Transaccion t) {
		transaccionDAO.save(t);
	}

	@Override
	public Transaccion findById(String id) {
		return transaccionDAO.findById(id).orElseThrow(() -> new TransaccionNoEncontradaException(id));
	}

	@Override
	public void delete(Transaccion t) {
		transaccionDAO.delete(t);
	}

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

	@Override
	public Transaccion comprarCrypto(String usuarioId, String simbolo, String nombreCrypto, double cantidadCrypto, double precioUnitario) {

		Portafolio portafolio = portafolioService.getPortafolioDeUsuarioId(usuarioId);
		double totalCompra = cantidadCrypto * precioUnitario;

		System.out.println("🧾 UsuarioId del portafolio: " + portafolio.getUsuarioId());
		System.out.println("🧾 ID del portafolio: " + portafolio.getId());
		
		if (portafolio == null) {
			System.out.println("⚠️ Portafolio no encontrado. Creando nuevo...");
			portafolio = new Portafolio();
			portafolio.setUsuarioId(usuarioId);
			portafolio.setSaldo(10000.0); // o el saldo inicial que manejes
		}

		System.out.println("🧮 Saldo del portafolio: " + portafolio.getSaldo());
		System.out.println("💸 Total compra: " + totalCompra);
		System.out.println("🧾 ID del portafolio: " + portafolio.getId());
		System.out.println("🧾 UsuarioId del portafolio: " + portafolio.getUsuarioId());

		if (portafolio.getSaldo() < totalCompra) {
			throw new RuntimeException("Saldo insuficiente para realizar la compra.");
		}

		// 1. Crear la transacción
		Transaccion transaccion = new Transaccion();
		transaccion.setUsuarioId(usuarioId);
		transaccion.setCryptoId(simbolo);
		transaccion.setTipoTransaccion(TransactionType.COMPRAR);
		transaccion.setCantidadCrypto(cantidadCrypto);
		transaccion.setPrecioTransaccion(precioUnitario);
		transaccion.setValorTotal(totalCompra);
		transaccion.setFechaTransaccion(LocalDateTime.now());

		System.out.println("📝 Guardando transacción...");
		transaccionDAO.save(transaccion);
		System.out.println("✅ Transacción guardada.");

		// 2. Actualizar el portafolio
		portafolio.agregarCripto(simbolo, cantidadCrypto);
		portafolio.setSaldo(portafolio.getSaldo() - totalCompra);
		System.out.println("🔁 Nuevo saldo: " + portafolio.getSaldo());

		portafolioService.save(portafolio);

		return transaccion;
	}



	@Override
	public double getTotalInvertido(String usuarioId) {
	    List<Transaccion> compras = transaccionDAO.findByUsuarioIdAndTipoTransaccion(usuarioId, TransactionType.COMPRAR);
	    return compras.stream()
	                  .mapToDouble(Transaccion::getValorTotal)
	                  .sum();
	}

	
	@Override
	public List<Transaccion> findByUsuarioId(String usuarioId) {
	    return transaccionDAO.findByUsuarioIdOrderByFechaTransaccionDesc(usuarioId);
	}

	@Override
	public Transaccion venderCrypto(String usuarioId, String simbolo, String nombreCrypto, double cantidadCrypto, double precioUnitario) {

	    Portafolio portafolio = portafolioService.getPortafolioDeUsuarioId(usuarioId);
	    System.out.println("🟠 Portafolio obtenido para venta: " + portafolio.getId());
	    System.out.println("📊 Criptos antes de venta: " + portafolio.getCriptomonedas());

	    // 1. Verificar que tiene suficiente cantidad
	    if (!portafolioService.tieneSuficienteCrypto(usuarioId, simbolo, cantidadCrypto)) {
	        throw new RuntimeException("No tienes suficiente " + simbolo + " para vender.");
	    }

	    double totalVenta = cantidadCrypto * precioUnitario;

	    // 2. Crear y guardar la transacción
	    Transaccion transaccion = new Transaccion();
	    transaccion.setUsuarioId(usuarioId);
	    transaccion.setCryptoId(simbolo);
	    transaccion.setTipoTransaccion(TransactionType.VENDER);
	    transaccion.setCantidadCrypto(cantidadCrypto);
	    transaccion.setPrecioTransaccion(precioUnitario);
	    transaccion.setValorTotal(totalVenta);
	    transaccion.setFechaTransaccion(LocalDateTime.now());

	    transaccionDAO.save(transaccion);
	    System.out.println("✅ Transacción de venta guardada");

	    // 3. ACTUALIZAR el mismo portafolio directamente
	    Map<String, Double> cryptos = portafolio.getCriptomonedas();
	    double actual = cryptos.getOrDefault(simbolo, 0.0);
	    double restante = actual - cantidadCrypto;

	    if (restante <= 0) {
	        cryptos.remove(simbolo);
	        System.out.println("❌ Cripto eliminada del portafolio");
	    } else {
	        cryptos.put(simbolo, restante);
	        System.out.println("🔁 Nueva cantidad de " + simbolo + ": " + restante);
	    }

	    // 4. Sumar el saldo
	    double nuevoSaldo = portafolio.getSaldo() + totalVenta;
	    portafolio.setSaldo(nuevoSaldo);
	    System.out.println("💰 Nuevo saldo del portafolio: " + nuevoSaldo);

	    // 5. Guardar el portafolio actualizado
	    portafolioService.save(portafolio);
	    System.out.println("💾 Portafolio actualizado y guardado correctamente");

	    return transaccion;
	}



}

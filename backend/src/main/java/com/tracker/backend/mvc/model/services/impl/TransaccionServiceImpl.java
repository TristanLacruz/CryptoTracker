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

	    System.out.println("🧮 Saldo del portafolio: " + portafolio.getSaldo());
	    System.out.println("💸 Total compra: " + totalCompra);
	    
	    System.out.println("🧾 ID del portafolio: " + portafolio.getId());
	    System.out.println("🧾 UsuarioId del portafolio: " + portafolio.getUsuarioId());


	    if (portafolio.getSaldo() < totalCompra) {
	        throw new RuntimeException("Saldo insuficiente para realizar la compra.");
	    }
	    
	    System.out.println("🧪 DEBUG saldo actual: " + portafolio.getSaldo());
	    System.out.println("🧪 DEBUG totalCompra: " + totalCompra);
	    System.out.println("🧪 DEBUG usuarioId recibido: " + usuarioId);


	    // 1. Crear la transacción
	    Transaccion transaccion = new Transaccion();
	    transaccion.setUsuarioId(usuarioId);
	    transaccion.setCryptoId(simbolo);
	    transaccion.setTipoTransaccion(TransactionType.COMPRAR);
	    transaccion.setCantidadCrypto(cantidadCrypto);
	    transaccion.setPrecioTransaccion(precioUnitario);
	    transaccion.setValorTotal(totalCompra);
	    transaccion.setFechaTransaccion(LocalDateTime.now());

	    transaccionDAO.save(transaccion);

	    // 2. Actualizar el portafolio
	    portafolio.agregarCripto(simbolo, cantidadCrypto);
	    portafolio.setSaldo(portafolio.getSaldo() - totalCompra);
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

	    // 1. Validar que el usuario tiene suficiente cantidad de esa cripto
	    if (!portafolioService.tieneSuficienteCrypto(usuarioId, simbolo, cantidadCrypto)) {
	        throw new RuntimeException("No tienes suficiente " + simbolo + " para vender.");
	    }

	    double totalVenta = cantidadCrypto * precioUnitario;

	    // 2. Crear la transacción
	    Transaccion transaccion = new Transaccion();
	    transaccion.setUsuarioId(usuarioId);
	    transaccion.setCryptoId(simbolo);
	    transaccion.setTipoTransaccion(TransactionType.VENDER);
	    transaccion.setCantidadCrypto(cantidadCrypto);
	    transaccion.setPrecioTransaccion(precioUnitario);
	    transaccion.setValorTotal(totalVenta);
	    transaccion.setFechaTransaccion(LocalDateTime.now());

	    transaccionDAO.save(transaccion);

	    // 3. Actualizar el portafolio
	    portafolioService.eliminarCrypto(usuarioId, simbolo, cantidadCrypto);
	    portafolio.setSaldo(portafolio.getSaldo() + totalVenta);
	    portafolioService.save(portafolio);

	    return transaccion;
	}

}

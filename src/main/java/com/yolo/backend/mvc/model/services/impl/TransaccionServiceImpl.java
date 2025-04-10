package com.yolo.backend.mvc.model.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yolo.backend.mvc.model.dao.ITransaccionDAO;
import com.yolo.backend.mvc.model.entity.Transaccion;
import com.yolo.backend.mvc.model.entity.Usuario;
import com.yolo.backend.mvc.model.exceptions.TransaccionNoEncontradaException;
import com.yolo.backend.mvc.model.services.ICriptomonedaService;
import com.yolo.backend.mvc.model.services.IPortafolioService;
import com.yolo.backend.mvc.model.services.ITransaccionService;
import com.yolo.backend.mvc.model.services.IUsuarioService;

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
	public Transaccion comprarCrypto(String usuarioId, String cryptoSimbolo, double cantidadUSD) {
		Usuario usuario = usuarioService.findById(usuarioId);
		double precio = cryptoService.getPrecioActual(cryptoSimbolo);
		double cantidad = cantidadUSD / precio;

		if (usuario.getSaldo() < cantidadUSD) {
			throw new RuntimeException("Saldo insuficiente.");
		}

		// Restar saldo
		usuario.setSaldo(usuario.getSaldo() - cantidadUSD);
		usuarioService.save(usuario);

		// Añadir cripto al portfolio
		portafolioService.anadirCrypto(usuarioId, cryptoSimbolo, cantidad);

		// Registrar transacción
		Transaccion tx = new Transaccion(usuarioId, cryptoSimbolo, "BUY", cantidad, precio);
		return transaccionDAO.save(tx);
	}

	@Override
	public Transaccion venderCrypto(String usuarioId, String cryptoSimbolo, double cantidadUSD) {
		Usuario usuario = usuarioService.findById(usuarioId);
		double precio = cryptoService.getPrecioActual(cryptoSimbolo);
		double cantidad = cantidadUSD / precio;

		if (!portafolioService.tieneSuficienteCrypto(usuarioId, cryptoSimbolo, cantidad)) {
			throw new RuntimeException("No tienes suficiente cantidad de " + cryptoSimbolo);
		}

		// Sumar saldo
		usuario.setSaldo(usuario.getSaldo() + cantidadUSD);
		usuarioService.save(usuario);

		// Quitar cripto del portfolio
		portafolioService.eliminarCrypto(usuarioId, cryptoSimbolo, cantidad);

		// Registrar transacción
		Transaccion tx = new Transaccion(usuarioId, cryptoSimbolo, "VENDER", cantidad, precio);
		return transaccionDAO.save(tx);
	}
	
	@Override
	public List<Transaccion> findByUsuarioId(String usuarioId) {
	    return transaccionDAO.findByUsuarioIdOrderByFechaTransaccionDesc(usuarioId);
	}

}

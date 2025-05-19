package com.tracker.backend.mvc.model.services;

import java.util.List;

import com.tracker.backend.mvc.model.entity.Transaccion;

public interface ITransaccionService {

public List<Transaccion> findAll();
	
	public void save(Transaccion t);
	public Transaccion findById(String id);
	public void delete(Transaccion t);
	public Transaccion update(Transaccion t, String id);
	
	public double getTotalInvertido(String usuarioId);
	
	Transaccion comprarCrypto(String uid, String simbolo, String nombreCrypto, double cantidadCrypto, double precioUnitario);
	Transaccion venderCrypto(String usuarioId, String simbolo, String nombreCrypto, double cantidadCrypto, double precioUnitario);

	List<Transaccion> findByUsuarioId(String usuarioId);

}

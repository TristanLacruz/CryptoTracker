package com.yolo.backend.mvc.model.services;

import java.util.List;

import com.yolo.backend.mvc.model.entity.Transaccion;

public interface ITransaccionService {

public List<Transaccion> findAll();
	
	public void save(Transaccion t);
	public Transaccion findById(String id);
	public void delete(Transaccion t);
	public Transaccion update(Transaccion t, String id);
	
	/*
	 * 
	 */
	Transaccion comprarCrypto(String usuarioId, String cryptoId, double cantidadUSD);
	Transaccion venderCrypto(String usuarioId, String cryptoId, double cantidadCrypto);
	List<Transaccion> findByUsuarioId(String usuarioId);

}

package com.yolo.backend.mvc.model.services;

import java.util.List;
import com.yolo.backend.mvc.model.entity.Portafolio;

public interface IPortafolioService {

	public List<Portafolio> findAll();
	public void save(Portafolio p);	
	public Portafolio findById(String id);
	public void delete(Portafolio p);
	public Portafolio update(Portafolio p, String id);
	
	void anadirCrypto(String usuarioId, String simbolo, double cantidad);
    void eliminarCrypto(String usuarioId, String simbolo, double cantidad);
    boolean tieneSuficienteCrypto(String usuarioId, String simbolo, double cantidad);
    Portafolio getPortafolioDeUsuarioId(String usuarioId);
	void updatePortafolioDespuesDeCompra(String usuarioId, String cryptoId, double cantidad);
}

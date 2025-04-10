package com.yolo.backend.mvc.model.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yolo.backend.mvc.model.dao.IPortafolioDAO;
import com.yolo.backend.mvc.model.entity.Portafolio;
import com.yolo.backend.mvc.model.exceptions.PortafolioNoEncontradoException;
import com.yolo.backend.mvc.model.services.IPortafolioService;

@Service
public class PortafolioServiceImpl implements IPortafolioService {

	@Autowired
	private IPortafolioDAO PortafolioDAO;

	@Override
	public List<Portafolio> findAll() {
		return (List<Portafolio>) PortafolioDAO.findAll();
	}

	@Override
	public void save(Portafolio p) {
		PortafolioDAO.save(p);
	}

	@Override
	public Portafolio findById(String id) {
		return PortafolioDAO.findById(id).orElseThrow(() -> new PortafolioNoEncontradoException(id));
	}

	@Override
	public void delete(Portafolio p) {
		PortafolioDAO.delete(p);
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
		Portafolio Portafolio = PortafolioDAO.findByUsuarioId(usuarioId).orElse(new Portafolio(usuarioId));

		Portafolio.getCriptomonedas().merge(simbolo, quantity, Double::sum);
		PortafolioDAO.save(Portafolio);
	}

	@Override
	public void eliminarCrypto(String usuarioId, String simbolo, double quantity) {
		Portafolio Portafolio = PortafolioDAO.findByUsuarioId(usuarioId)
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

		PortafolioDAO.save(Portafolio);
	}

	@Override
	public boolean tieneSuficienteCrypto(String usuarioId, String simbolo, double quantity) {
		Portafolio Portafolio = PortafolioDAO.findByUsuarioId(usuarioId).orElse(new Portafolio(usuarioId));

		return Portafolio.getCriptomonedas().getOrDefault(simbolo, 0.0) >= quantity;
	}

	@Override
	public Portafolio getPortafolioDeUsuarioId(String usuarioId) {
		return PortafolioDAO.findByUsuarioId(usuarioId).orElse(new Portafolio(usuarioId));
	}
	
	@Override
	public void updatePortafolioDespuesDeCompra(String usuarioId, String cryptoId, double cantidadCrypto) {
	    Portafolio Portafolio = PortafolioDAO.findById(usuarioId)
	            .orElseGet(() -> {
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
	    PortafolioDAO.save(Portafolio);
	}


}

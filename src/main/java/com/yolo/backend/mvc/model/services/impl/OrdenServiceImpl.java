package com.yolo.backend.mvc.model.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yolo.backend.mvc.model.dao.IOrdenDAO;
import com.yolo.backend.mvc.model.entity.Orden;
import com.yolo.backend.mvc.model.exceptions.OrdenNoEncontradaException;
import com.yolo.backend.mvc.model.services.IOrdenService;
import com.yolo.backend.mvc.model.services.IPortafolioService;

@Service
public class OrdenServiceImpl implements IOrdenService {

	@Autowired
	private IOrdenDAO OrdenDAO;

	@Autowired
	private IPortafolioService portfolioService;

	@Override
	public List<Orden> findAll() {
		return (List<Orden>) OrdenDAO.findAll();
	}

	@Override
	public void save(Orden o) {
		OrdenDAO.save(o);
	}

	@Override
	public Orden findById(String id) {
		return OrdenDAO.findById(id).orElseThrow(() -> new OrdenNoEncontradaException(id));
	}

	@Override
	public void delete(Orden o) {
		OrdenDAO.delete(o);
	}

	@Override
	public Orden update(Orden o, String id) {
		Orden ordenActual = this.findById(id);
		ordenActual.setCantidad(o.getCantidad());
		ordenActual.setCreadoEl(o.getCreadoEl());
		ordenActual.setCryptoId(o.getCryptoId());
		ordenActual.setEjecutadoEl(o.getEjecutadoEl());
		ordenActual.setEstado(o.getEstado());
		ordenActual.setObjetivoPrecio(o.getObjetivoPrecio());
		ordenActual.setTipo(o.getTipo());
		ordenActual.setUsuarioId(o.getUsuarioId());
		this.save(ordenActual);
		return ordenActual;
	}

	public void executeOrden(Orden Orden) {
		if (!"ejecutada".equals(Orden.getEstado())) {
			Orden.setEstado(null);
			Orden.setEjecutadoEl(null);

			if ("MERCADO".equalsIgnoreCase(Orden.getTipo())) {
				if (Orden.getCantidad() > 0) {
					// Simulación de compra: se añade la cantidad al portfolio
					portfolioService.anadirCrypto(Orden.getUsuarioId(), Orden.getCryptoId(), Orden.getCantidad());
				} else {
					// Simulación de venta: se elimina la cantidad del portfolio
					portfolioService.eliminarCrypto(Orden.getUsuarioId(), Orden.getCryptoId(),
							Math.abs(Orden.getCantidad()));
				}
			}

			OrdenDAO.save(Orden);
		}
	}
}

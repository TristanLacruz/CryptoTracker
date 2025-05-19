package com.tracker.backend.mvc.model.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tracker.backend.mvc.model.dao.IAlertaDAO;
import com.tracker.backend.mvc.model.entity.Alerta;
import com.tracker.backend.mvc.model.exceptions.AlertaNoEncontradaException;
import com.tracker.backend.mvc.model.services.IAlertaService;

@Service
public class AlertaServiceImpl implements IAlertaService {

	@Autowired
	private IAlertaDAO AlertaDAO;
	
	@Override
	public List <Alerta> findAll(){
		return (List<Alerta>)AlertaDAO.findAll();
	}

	@Override
	public void save(Alerta a) {
		AlertaDAO.save(a);
	}

	@Override
	public Alerta findById(String id) {
		return AlertaDAO.findById(id)
				.orElseThrow(() -> new AlertaNoEncontradaException(id));
	}

	@Override
	public void delete(Alerta a) {
		AlertaDAO.delete(a);
	}

	@Override
	public Alerta update(Alerta a, String id) {
		Alerta alertaActual = this.findById(id);
		alertaActual.setCreadoEl(a.getCreadoEl());
		alertaActual.setCryptoId(a.getCryptoId());
		alertaActual.setObjetivoPrecio(a.getObjetivoPrecio());
		alertaActual.setEjecutado(a.getEjecutado());
		alertaActual.setUsuarioId(a.getUsuarioId());
		this.save(alertaActual);
		return alertaActual;
	}
}

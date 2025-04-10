package com.yolo.backend.mvc.model.services;

import java.util.List;
import com.yolo.backend.mvc.model.entity.Alerta;

public interface IAlertaService {

	public List<Alerta> findAll();

	public void save(Alerta a);

	public Alerta findById(String id);

	public void delete(Alerta a);

	public Alerta update(Alerta a, String id);
}

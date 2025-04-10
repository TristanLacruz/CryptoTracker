package com.yolo.backend.mvc.model.services;

import java.util.List;

import org.springframework.stereotype.Service;
import com.yolo.backend.mvc.model.entity.Orden;

@Service
public interface IOrdenService {
	
	public List <Orden> findAll();
	
	public void save(Orden o);
	
	public Orden findById(String id);
	
	public void delete(Orden o);
	
	public Orden update(Orden o, String id);
}
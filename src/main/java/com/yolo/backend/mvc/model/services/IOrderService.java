package com.yolo.backend.mvc.model.services;

import java.util.List;

import org.springframework.stereotype.Service;
import com.yolo.backend.mvc.model.entity.Order;

@Service
public interface IOrderService {
	
	public List <Order> findAll();
	
	public void save(Order o);
	
	public Order findById(String id);
	
	public void delete(Order o);
	
	public Order update(Order o, String id);
}
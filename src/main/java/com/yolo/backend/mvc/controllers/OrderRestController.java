package com.yolo.backend.mvc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.backend.mvc.model.entity.Order;
import com.yolo.backend.mvc.model.services.IOrderService;
import com.yolo.backend.mvc.model.services.OrderServiceImpl;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("")
public class OrderRestController {

	@Autowired
	private IOrderService orderService;
	
	@GetMapping("/orders")
	public List<Order> getOrders(){
		return orderService.findAll();
	}
	
	@PostMapping("/orders")
	@ResponseStatus(HttpStatus.CREATED)
	public Order createOrder(@RequestBody Order order) {
		orderService.save(order);
		return order;
	}
	
	@PostMapping("/orders/{id}/execute")
	@ResponseStatus(HttpStatus.OK)
	public Order executeOrder(@PathVariable String id) {
	    Order order = orderService.findById(id);
	    ((OrderServiceImpl) orderService).executeOrder(order);
	    return order;
	}

}

package com.yolo.backend.mvc.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yolo.backend.mvc.model.dao.IOrderDAO;
import com.yolo.backend.mvc.model.entity.Order;
import com.yolo.backend.mvc.model.exceptions.OrderNotFoundException;

@Service
public class OrderServiceImpl implements IOrderService {

	@Autowired
	private IOrderDAO orderDAO;
	
	@Autowired
	private IPortfolioService portfolioService;
	
	@Override
	public List <Order> findAll(){
		return (List<Order>)orderDAO.findAll();
	}

	@Override
	public void save(Order o) {
		orderDAO.save(o);
	}

	@Override
	public Order findById(String id) {
		return orderDAO.findById(id)
				.orElseThrow(() -> new OrderNotFoundException(id));
	}

	@Override
	public void delete(Order o) {
		orderDAO.delete(o);
	}

	@Override
	public Order update(Order o, String id) {
		Order currentOrder = this.findById(id);
		currentOrder.setAmount(o.getAmount());
		currentOrder.setCreatedAt(o.getCreatedAt());
		currentOrder.setCryptoId(o.getCryptoId());
		currentOrder.setExecutedAt(o.getExecutedAt());
		currentOrder.setStatus(o.getStatus());
		currentOrder.setTargetPrice(o.getTargetPrice());
		currentOrder.setType(o.getType());
		currentOrder.setUserId(o.getUserId());
		this.save(currentOrder);
		return currentOrder;
	}
	
	public void executeOrder(Order order) {
	    if (!"executed".equals(order.getStatus())) {
	        order.setStatus("executed");
	        order.setExecutedAt(java.time.LocalDateTime.now());

	        if ("MARKET".equalsIgnoreCase(order.getType())) {
	            if (order.getAmount() > 0) {
	                // Simulación de compra: se añade la cantidad al portfolio
	                portfolioService.addCrypto(order.getUserId(), order.getCryptoId(), order.getAmount());
	            } else {
	                // Simulación de venta: se elimina la cantidad del portfolio
	                portfolioService.removeCrypto(order.getUserId(), order.getCryptoId(), Math.abs(order.getAmount()));
	            }
	        }

	        orderDAO.save(order);
	    }
}

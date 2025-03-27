package com.yolo.backend.mvc.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.yolo.backend.mvc.model.entity.Order;

public interface IOrderDAO extends MongoRepository<Order, String>{

}

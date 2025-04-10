package com.yolo.backend.mvc.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.yolo.backend.mvc.model.entity.Orden;

public interface IOrdenDAO extends MongoRepository<Orden, String>{

}

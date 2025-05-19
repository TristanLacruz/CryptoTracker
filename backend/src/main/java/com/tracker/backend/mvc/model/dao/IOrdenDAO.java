package com.tracker.backend.mvc.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.tracker.backend.mvc.model.entity.Orden;

public interface IOrdenDAO extends MongoRepository<Orden, String>{

}

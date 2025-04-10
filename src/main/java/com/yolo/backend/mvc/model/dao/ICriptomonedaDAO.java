package com.yolo.backend.mvc.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.yolo.backend.mvc.model.entity.Criptomoneda;

public interface ICriptomonedaDAO extends MongoRepository<Criptomoneda, String>{

}

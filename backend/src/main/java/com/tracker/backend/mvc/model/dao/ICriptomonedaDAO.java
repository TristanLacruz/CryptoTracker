package com.tracker.backend.mvc.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.tracker.backend.mvc.model.entity.Criptomoneda;


public interface ICriptomonedaDAO extends MongoRepository<Criptomoneda, String>{


}

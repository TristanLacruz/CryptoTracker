package com.yolo.backend.mvc.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.yolo.backend.mvc.model.entity.Cryptocurrency;

public interface ICryptocurrencyDAO extends MongoRepository<Cryptocurrency, String>{

}

package com.tracker.backend.mvc.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.tracker.backend.mvc.model.entity.Alerta;

public interface IAlertaDAO extends MongoRepository<Alerta, String>{

}

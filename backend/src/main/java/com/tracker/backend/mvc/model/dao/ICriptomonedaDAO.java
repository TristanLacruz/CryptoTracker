package com.tracker.backend.mvc.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.tracker.backend.mvc.model.entity.Criptomoneda;

/**
 * Interfaz para el acceso a datos de criptomonedas.
 * Extiende de MongoRepository para proporcionar operaciones CRUD básicas.
 */
public interface ICriptomonedaDAO extends MongoRepository<Criptomoneda, String>{


}

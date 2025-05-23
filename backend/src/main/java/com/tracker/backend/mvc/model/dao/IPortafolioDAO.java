package com.tracker.backend.mvc.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tracker.backend.mvc.model.entity.Portafolio;

public interface IPortafolioDAO extends MongoRepository<Portafolio, String> {

    Optional<Portafolio> findByUsuarioId(String userId);

}

package com.yolo.backend.mvc.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yolo.backend.mvc.model.entity.Portfolio;

public interface IPortfolioDAO extends MongoRepository<Portfolio, String> {

    Optional<Portfolio> findByUserId(String userId);
}

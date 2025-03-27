package com.yolo.backend.mvc.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yolo.backend.mvc.model.entity.Transaction;

public interface ITransactionDAO extends MongoRepository<Transaction, String> {

}

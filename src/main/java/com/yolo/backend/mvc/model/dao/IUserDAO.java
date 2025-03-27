package com.yolo.backend.mvc.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.yolo.backend.mvc.model.entity.User;

public interface IUserDAO extends MongoRepository<User, String>{

}

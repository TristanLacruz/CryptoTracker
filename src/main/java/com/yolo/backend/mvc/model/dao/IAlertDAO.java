package com.yolo.backend.mvc.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.yolo.backend.mvc.model.entity.Alert;

public interface IAlertDAO extends MongoRepository<Alert, String>{

}

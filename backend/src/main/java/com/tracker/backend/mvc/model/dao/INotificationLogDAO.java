package com.tracker.backend.mvc.model.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tracker.backend.mvc.model.entity.NotificationLog;

public interface INotificationLogDAO extends MongoRepository<NotificationLog, String>{

}

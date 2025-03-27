package com.yolo.backend.mvc.model.services;

import java.util.List;
import com.yolo.backend.mvc.model.entity.NotificationLog;

public interface INotificationLogService {

	public List<NotificationLog> findAll();
	
	public void save(NotificationLog n);
	
	public NotificationLog findById(String id);
	
	public void delete(NotificationLog n);
	
	public NotificationLog update(NotificationLog n, String id);
}

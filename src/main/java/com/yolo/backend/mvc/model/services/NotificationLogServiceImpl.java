package com.yolo.backend.mvc.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yolo.backend.mvc.model.dao.INotificationLogDAO;
import com.yolo.backend.mvc.model.entity.NotificationLog;
import com.yolo.backend.mvc.model.exceptions.NotificationLogNotFoundException;

@Service
public class NotificationLogServiceImpl implements INotificationLogService {

	@Autowired
	private INotificationLogDAO notificationDAO;
	
	@Override
	public List<NotificationLog> findAll() {
		return (List<NotificationLog>)notificationDAO.findAll();
	}

	@Override
	public void save(NotificationLog n) {
		notificationDAO.save(n);
	}

	@Override
	public NotificationLog findById(String id) {
		return notificationDAO.findById(id)
				.orElseThrow(() -> new NotificationLogNotFoundException(id));
	}

	@Override
	public void delete(NotificationLog n) {
		notificationDAO.delete(n);
	}

	@Override
	public NotificationLog update(NotificationLog n, String id) {
		NotificationLog currentNotification = this.findById(id);
		currentNotification.setUserId(n.getUserId());
		currentNotification.setMessageContent(n.getMessageContent());
		currentNotification.setSentAt(n.getSentAt());
		return currentNotification;
	}

}

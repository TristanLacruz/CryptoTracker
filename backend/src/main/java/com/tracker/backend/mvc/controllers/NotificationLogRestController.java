package com.tracker.backend.mvc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tracker.backend.mvc.model.entity.NotificationLog;
import com.tracker.backend.mvc.model.services.INotificationLogService;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("")
public class NotificationLogRestController {

	@Autowired
	private INotificationLogService notificationService;
	
	@GetMapping("/notifications")
	public List<NotificationLog> getNotificationLogs(){
		return notificationService.findAll();
	}
	
	@PostMapping("/notifications")
	@ResponseStatus(HttpStatus.CREATED)
    public NotificationLog createNotificationLog(@RequestBody NotificationLog notificationLog) {
		notificationService.save(notificationLog);
        return notificationLog;
    }

}

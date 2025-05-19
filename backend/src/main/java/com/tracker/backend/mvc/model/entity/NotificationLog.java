package com.tracker.backend.mvc.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "notification_logs")
public class NotificationLog {

	@Id
	private String id;
	private String userId;
	private String messageContent;
	private LocalDateTime sentAt;

	public NotificationLog() {
		this.sentAt = LocalDateTime.now();
	}

	public NotificationLog(String id) {
		this.id = id;
	}

	public NotificationLog(String userId, String messageContent) {
		this.userId = userId;
		this.messageContent = messageContent;
		this.sentAt = LocalDateTime.now();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public LocalDateTime getSentAt() {
		return sentAt;
	}

	public void setSentAt(LocalDateTime sentAt) {
		this.sentAt = sentAt;
	}

	
	
}

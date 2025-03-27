package com.yolo.backend.mvc.model.services;

import java.util.List;

import com.yolo.backend.mvc.model.entity.User;

public interface IUserService {

	public List<User> findAll();
	
	public void save(User u);
	
	public User findById(String id);
	
	public void delete(User u);
	
	public User update(User u, String id);
}

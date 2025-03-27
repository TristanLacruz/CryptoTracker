package com.yolo.backend.mvc.model.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yolo.backend.mvc.model.dao.IUserDAO;
import com.yolo.backend.mvc.model.entity.User;
import com.yolo.backend.mvc.model.exceptions.UserNotFoundException;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserDAO userDAO;
	
	@Override
	public List <User> findAll(){
		return (List<User>)userDAO.findAll();
	}

	@Override
	public void save(User u) {
		userDAO.save(u);
	}

	@Override
	public User findById(String id) {
		return userDAO.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
	}

	@Override
	public void delete(User u) {
		userDAO.delete(u);
	}

	@Override
	public User update(User u, String id) {
		User currentUser = this.findById(id);
		currentUser.setUsername(u.getUsername());
		currentUser.setEmail(u.getEmail());
		currentUser.setName(u.getName());
		currentUser.setSurname(u.getSurname());
		currentUser.setPassword(u.getPassword());
		currentUser.setRoles(u.getRoles());
		currentUser.setTelegramChatId(u.getTelegramChatId());
		currentUser.setBalance(u.getBalance());
		currentUser.setCreatedAt(u.getCreatedAt());
		currentUser.setUpdatedAt(u.getUpdatedAt());
		this.save(currentUser);
		return currentUser;
	}
}

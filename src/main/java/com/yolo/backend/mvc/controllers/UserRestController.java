package com.yolo.backend.mvc.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.yolo.backend.mvc.model.entity.User;
import com.yolo.backend.mvc.model.services.IUserService;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("")
public class UserRestController {

	@Autowired
	private IUserService userService;
	
	@GetMapping("/users")
	public List<User> getUsers(){
		return userService.findAll();
	}
	
	@PostMapping("/users")
	@ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        userService.save(user);
        return user;
    }

}

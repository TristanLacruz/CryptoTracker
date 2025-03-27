package com.yolo.backend.mvc.controllers;

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
import com.yolo.backend.mvc.model.entity.Alert;
import com.yolo.backend.mvc.model.services.IAlertService;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("")
public class AlertRestController {
	

	@Autowired
	private IAlertService alertService;
	
	@GetMapping("/alerts")
	public List<Alert> getUsers(){
		return alertService.findAll();
	}
	
	@PostMapping("/alerts")
	@ResponseStatus(HttpStatus.CREATED)
    public Alert createUser(@RequestBody Alert alert) {
        alertService.save(alert);
        return alert;
    }
}

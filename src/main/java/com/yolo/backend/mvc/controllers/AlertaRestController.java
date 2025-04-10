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
import com.yolo.backend.mvc.model.entity.Alerta;
import com.yolo.backend.mvc.model.services.IAlertaService;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("")
public class AlertaRestController {
	

	@Autowired
	private IAlertaService alertaService;
	
	@GetMapping("/alertas")
	public List<Alerta> getUsers(){
		return alertaService.findAll();
	}
	
	@PostMapping("/alertas")
	@ResponseStatus(HttpStatus.CREATED)
    public Alerta createUser(@RequestBody Alerta alerta) {
        alertaService.save(alerta);
        return alerta;
    }
}

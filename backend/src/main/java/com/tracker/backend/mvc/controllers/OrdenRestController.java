package com.tracker.backend.mvc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.tracker.backend.mvc.model.entity.Orden;
import com.tracker.backend.mvc.model.services.IOrdenService;
import com.tracker.backend.mvc.model.services.impl.OrdenServiceImpl;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("")
public class OrdenRestController {

	@Autowired
	private IOrdenService ordenService;
	
	@GetMapping("/ordenes")
	public List<Orden> getOrdens(){
		return ordenService.findAll();
	}
	
	@PostMapping("/ordenes")
	@ResponseStatus(HttpStatus.CREATED)
	public Orden createOrden(@RequestBody Orden Orden) {
		ordenService.save(Orden);
		return Orden;
	}
	
	@PostMapping("/ordenes/{id}/ejecutar")
	@ResponseStatus(HttpStatus.OK)
	public Orden executeOrden(@PathVariable String id) {
	    Orden orden = ordenService.findById(id);
	    ((OrdenServiceImpl) ordenService).executeOrden(orden);
	    return orden;
	}

}

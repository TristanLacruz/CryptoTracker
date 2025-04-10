package com.yolo.backend.mvc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.yolo.backend.mvc.model.entity.Transaccion;
import com.yolo.backend.mvc.model.services.ITransaccionService;

@CrossOrigin(origins="*")
@RestController
@RequestMapping("/api/transacciones")
public class TransaccionRestController {

	@Autowired
	private ITransaccionService transaccionService;
	
	@GetMapping
	public List<Transaccion> getTransaccion(){
		return transaccionService.findAll();
	}
	
	@PostMapping("/transaccion")
	@ResponseStatus(HttpStatus.CREATED)
    public Transaccion createTransaccion(@RequestBody Transaccion Transaccion) {
		transaccionService.save(Transaccion);
        return Transaccion;
    }
	
	@PostMapping("/comprar")
	public Transaccion buyCrypto(@RequestParam String usuarioId,
	                             @RequestParam String simbolo,
	                             @RequestParam double cantidadUSD) {
	    if (cantidadUSD <= 0) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor que 0");
	    }
	    return transaccionService.comprarCrypto(usuarioId, simbolo, cantidadUSD);
	}


	@PostMapping("/vender")
	public Transaccion sellCrypto(@RequestParam String usuarioId,
	                              @RequestParam String simbolo,
	                              @RequestParam double cantidadUSD) {
	    if (cantidadUSD <= 0) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor que 0");
	    }
	    return transaccionService.venderCrypto(usuarioId, simbolo, cantidadUSD);
	}

    
    @GetMapping("/usuario")
    public List<Transaccion> getByUsuario(@RequestParam String usuarioId) {
        return transaccionService.findByUsuarioId(usuarioId);
    }

    @GetMapping("/mis-transacciones")
    public List<Transaccion> getMisTransacciones(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        return transaccionService.findByUsuarioId(uid);
    }



}

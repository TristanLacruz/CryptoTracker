package com.yolo.backend.mvc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.backend.mvc.model.entity.Portafolio;
import com.yolo.backend.mvc.model.services.IPortafolioService;

@RestController
@RequestMapping("/api/portafolio")
@CrossOrigin(origins = "*")
public class PortafolioRestController {

    @Autowired
    private IPortafolioService portafolioService;

    @GetMapping("")
    public List<Portafolio> getUsuarios() {
        return portafolioService.findAll();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Portafolio crearPortafolio(@RequestBody Portafolio portafolio) {
        portafolioService.save(portafolio);
        return portafolio;
    }

    @GetMapping("/{usuarioId}")
    public Portafolio getPortafolio(@PathVariable String usuarioId) {
        return portafolioService.getPortafolioDeUsuarioId(usuarioId);
    }
}

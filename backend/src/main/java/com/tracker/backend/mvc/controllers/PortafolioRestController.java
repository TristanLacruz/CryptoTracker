package com.tracker.backend.mvc.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tracker.backend.mvc.model.entity.Portafolio;
import com.tracker.backend.mvc.model.services.IPortafolioService;
import com.tracker.common.dto.CriptoPosesionDTO;
import com.tracker.common.dto.EvolucionCompletaDTO;
import com.tracker.common.dto.RendimientoDiarioDTO;
import com.tracker.common.dto.ValorDiarioDTO;
import com.tracker.common.dto.OperacionDTO;
import com.tracker.backend.mvc.model.services.ICriptomonedaService;

@RestController
@RequestMapping("/api/portafolio")
@CrossOrigin(origins = "*")
public class PortafolioRestController {

    @Autowired
    private IPortafolioService portafolioService;

    @Autowired
    private ICriptomonedaService cryptoService; // el que tenga getPrecioActual(String simbolo)

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
    public ResponseEntity<?> getPortafolio(@PathVariable String usuarioId) {
        Portafolio p = portafolioService.getPortafolioDeUsuarioId(usuarioId);
        System.out.println("🌐 Endpoint /portafolios llamado con usuarioId: " + usuarioId);
        if (p == null) {
            return ResponseEntity.status(404)
                  .body(Map.of("estado","error","mensaje","Portafolio no encontrado"));
        }
        Map<String,Object> resp = Map.of(
            "saldo", p.getSaldo(),
            "criptomonedas", p.getCriptomonedas()
        );
        return ResponseEntity.ok(resp);
    }
    
    @GetMapping("/{usuarioId}/resumen")
    public List<CriptoPosesionDTO> getResumenPortafolio(@PathVariable String usuarioId) {
        Portafolio portafolio = portafolioService.getPortafolioDeUsuarioId(usuarioId);
        System.out.println("🔍 Criptomonedas en portafolio de " + usuarioId + ": " + portafolio.getCriptomonedas());
        return portafolio.getCriptomonedas().entrySet().stream()
            .map(entry -> {
                CriptoPosesionDTO dto = new CriptoPosesionDTO();
                dto.setSimbolo(entry.getKey());
                dto.setCantidad(entry.getValue());

                try {
                    double precio = cryptoService.getPrecioActual(entry.getKey());
                    dto.setValorTotal(entry.getValue() * precio);
                } catch (Exception e) {
                    dto.setValorTotal(0);
                }

                return dto;
            }).toList();
    }


    @GetMapping("/{usuarioId}/evolucion")
    public List<ValorDiarioDTO> getEvolucion(@PathVariable String usuarioId) {
        return portafolioService.calcularEvolucion(usuarioId);
    }

    @GetMapping("/{usuarioId}/rendimiento")
    public List<RendimientoDiarioDTO> getRendimiento(@PathVariable String usuarioId) {
        return portafolioService.calcularRendimiento(usuarioId);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMiPortafolio(Authentication auth) {
        String userId = auth.getName();
    	System.out.println("[DEBUG] getMiPortafolio para userId=" + userId);
    	Portafolio p = portafolioService.getPortafolioDeUsuarioId(auth.getName());
        if (p == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("estado","error","mensaje","Portafolio no encontrado"));
        }
        return ResponseEntity.ok(p);
    }
    
    @GetMapping("/me/resumen")
    public List<CriptoPosesionDTO> getMiResumen(Authentication auth) {
        return getResumenPortafolio(auth.getName());
    }



    @GetMapping("/{usuarioId}/evolucion-completa")
    public List<EvolucionCompletaDTO> getEvolucionCompleta(@PathVariable String usuarioId) {
        return portafolioService.calcularEvolucionCompleta(usuarioId);
    }
    
//    @GetMapping("/portafolios/{usuarioId}")
//    public ResponseEntity<?> obtenerPortafolio(@PathVariable String usuarioId) {
//        try {
//            Portafolio portafolio = portafolioService.findByUsuarioId(usuarioId);
//            return ResponseEntity.ok(portafolio);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Portafolio no encontrado para el usuario: " + usuarioId);
//        }
//    }


}

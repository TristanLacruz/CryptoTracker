//package com.tracker.backend.mvc.controllers;
//
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.tracker.backend.mvc.model.entity.Transaccion;
//import com.tracker.backend.mvc.model.services.IPortafolioService;
//
//@RestController
//@RequestMapping("/api/transacciones/test")
//public class TransaccionTestController {
//
//	@Autowired
//	private  IPortafolioService portafolioService;
//	
//    @PostMapping("/actualizar-portafolio")
//    public ResponseEntity<?> actualizarPortafolio(@RequestBody Transaccion transaccion) {
//        portafolioService.actualizarPortafolioConTransaccion(transaccion);
//        return ResponseEntity.ok(Map.of("estado", "ok"));
//    }
//}

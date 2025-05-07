package com.yolo.backend.mvc.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.yolo.backend.mvc.model.dto.UsuarioDTO;
import com.yolo.backend.mvc.model.entity.Usuario;
import com.yolo.backend.security.FirebaseService;
import com.yolo.backend.mvc.model.services.IUsuarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private IUsuarioService usuarioService;

    @GetMapping("/me/details")
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            FirebaseToken decodedToken = firebaseService.verifyToken(token);
            String firebaseUid = decodedToken.getUid();

            Usuario usuario = usuarioService.getOrCreateByUid(firebaseUid);
            UsuarioDTO dto = new UsuarioDTO(usuario);  // 👈 Convertimos a DTO aquí

            return ResponseEntity.ok(dto); // 👈 Devolvemos solo los campos necesarios

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401).body("Token inválido: " + e.getMessage());
        }
    }
}

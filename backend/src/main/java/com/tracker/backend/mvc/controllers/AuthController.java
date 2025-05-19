package com.tracker.backend.mvc.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.tracker.backend.mvc.model.AuthRequest;
import com.tracker.backend.mvc.model.entity.Usuario;
import com.tracker.backend.mvc.model.services.IUsuarioService;
import com.tracker.backend.security.FirebaseService;
import com.tracker.common.dto.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	@Autowired
	private FirebaseService firebaseService;

	@Autowired
	private IUsuarioService usuarioService;

	@GetMapping("/me/details")
	public UsuarioDTO obtenerDetalles(@RequestAttribute("usuarioFirebase") String uid) {
	    Usuario usuario = usuarioService.findByUid(uid)
	                                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con uid: " + uid));
	    return new UsuarioDTO(usuario.getUid(), usuario.getEmail());
	}


//	@GetMapping("/me/details")
//	public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String authorizationHeader) {
//		try {
//			String token = authorizationHeader.replace("Bearer ", "");
//			FirebaseToken decodedToken = firebaseService.verifyToken(token);
//			String firebaseUid = decodedToken.getUid();
//
//			Usuario usuario = usuarioService.getOrCreateByUid(firebaseUid);
//			UsuarioDTO dto = new UsuarioDTO(usuario); // 👈 Convertimos a DTO aquí
//
//			return ResponseEntity.ok(dto); // 👈 Devolvemos solo los campos necesarios
//
//		} catch (FirebaseAuthException e) {
//			return ResponseEntity.status(401).body("Token inválido: " + e.getMessage());
//		}
//	}

	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        // Aquí puedes implementar lógica adicional si haces login manual
        // Si ya usas Firebase para autenticación desde el frontend, puedes omitir esto
        return ResponseEntity.ok("Login request recibido con email: " + request.getEmail());
    }
}
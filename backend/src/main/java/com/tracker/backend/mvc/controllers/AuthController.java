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

	/**
	 * Endpoint para obtener los detalles del usuario autenticado.
	 * 
	 * @param uid el ID de usuario de Firebase
	 * @return un objeto UsuarioDTO con los detalles del usuario
	 */
	@GetMapping("/me/details")
	public UsuarioDTO obtenerDetalles(@RequestAttribute("usuarioFirebase") String uid) {
	    Usuario usuario = usuarioService.findByUid(uid)
	                                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con uid: " + uid));
	    return new UsuarioDTO(usuario.getUid(), usuario.getEmail());
	}

	/**
	 * Endpoint para iniciar sesión. Este método es un ejemplo y puede ser
	 * personalizado según tus necesidades.
	 * 
	 * @param request el objeto AuthRequest que contiene el email y la contraseña
	 * @return una respuesta de éxito o error
	 */
	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok("Login request recibido con email: " + request.getEmail());
    }
}
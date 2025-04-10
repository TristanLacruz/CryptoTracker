package com.yolo.backend.mvc.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yolo.backend.mvc.model.entity.Usuario;
import com.yolo.backend.mvc.model.services.IUsuarioService;

import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UsuarioRestController {

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private PasswordEncoder passwordEncoder; // <-- inyecta el PasswordEncoder

	@GetMapping("/usuarios")
	public List<Usuario> getUsuarios() {
		return usuarioService.findAll();
	}

	@PostMapping("/usuarios")
	@ResponseStatus(HttpStatus.CREATED)
	public Usuario createUser(@RequestBody Usuario usuario) {
		usuarioService.save(usuario);
		return usuario;
	}

	@GetMapping("/perfil")
	public Usuario perfilUsuario(Authentication auth) {
		return (Usuario) auth.getPrincipal();
	}

//	@PutMapping("/usuarios/{id}")
//	public ResponseEntity<Usuario> updateUser(@PathVariable String id, @RequestBody Usuario updatedData) {
//	    Usuario updatedUser = usuarioService.update(updatedData, id);
//	    return ResponseEntity.ok(updatedUser);
//	}

	@PutMapping("/usuarios/{id}")
	public Usuario updateUsuario(@RequestBody Usuario usuario, @PathVariable String id) {
	    return usuarioService.update(usuario, id);
	}


	@PostMapping("/recover")
	public ResponseEntity<?> recoverPassword(@RequestBody Map<String, String> payload) {
		String email = payload.get("email");
		// Aquí se debería generar un token de recuperación y enviarlo por email.
		// Para este ejemplo, simulamos generando un token y retornándolo.
		Optional<Usuario> optionalUser = usuarioService.findByEmail(email); // Asegúrate de implementar findByEmail en
																			// tu servicio/DAO.
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró usuario con ese email");
		}
		String resetToken = UUID.randomUUID().toString();
		// En producción, guardar el token con su fecha de expiración y enviarlo por
		// email.
		// Por ahora, lo devolvemos en la respuesta.
		Map<String, String> response = new HashMap<>();
		response.put("resetToken", resetToken);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/reset")
	public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
		String resetToken = payload.get("resetToken");
		String newPassword = payload.get("newPassword");
		// Aquí deberías validar el token recibido.
		// Para el ejemplo, asumimos que el token es válido y está asociado a un email.
		// En producción, deberás buscar el token almacenado y verificar su validez.
		String email = "correo_asociado_al_token"; // Esto es simulado
		Optional<Usuario> optionalUser = usuarioService.findByEmail(email);
		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró usuario con ese email");
		}
		Usuario user = optionalUser.get();
		user.setContrasena(passwordEncoder.encode(newPassword));
		usuarioService.save(user);
		return ResponseEntity.ok("Contraseña actualizada exitosamente");
	}
	
	@GetMapping("/usuarios/me")
	public ResponseEntity<?> getUsuarioActual(Authentication authentication) {
	    String uid = (String) authentication.getPrincipal();
	    Optional<Usuario> optionalUsuario = usuarioService.findByUid(uid);

	    if (optionalUsuario.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
	    }

	    return ResponseEntity.ok(optionalUsuario.get());
	}


}

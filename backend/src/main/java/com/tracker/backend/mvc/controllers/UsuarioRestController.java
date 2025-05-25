package com.tracker.backend.mvc.controllers;

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
import org.springframework.web.server.ResponseStatusException;
import com.tracker.backend.mvc.model.entity.Usuario;
import com.tracker.backend.mvc.model.services.IUsuarioService;
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
	public void createUser(@RequestBody Usuario usuario) {
		try {
			usuarioService.save(usuario); // ✅ no devuelve nada
		} catch (IllegalStateException | IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
	}

	@GetMapping("/perfil")
	public Usuario perfilUsuario(Authentication auth) {
		return (Usuario) auth.getPrincipal();
	}

	@PutMapping("/usuarios/{id}")
	public Usuario updateUsuario(@RequestBody Usuario usuario, @PathVariable String id) {
		return usuarioService.update(usuario, id);
	}

	@PostMapping("/recover")
	public ResponseEntity<?> recoverPassword(@RequestBody Map<String, String> payload) {
		String email = payload.get("email");
		Optional<Usuario> optionalUser = usuarioService.findByEmail(email);

		if (optionalUser.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró usuario con ese email");
		}
		String resetToken = UUID.randomUUID().toString();

		Map<String, String> response = new HashMap<>();
		response.put("resetToken", resetToken);
		return ResponseEntity.ok(response);
	}

	@PutMapping("/reset")
	public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> payload) {
		String resetToken = payload.get("resetToken");
		String newPassword = payload.get("newPassword");
		String email = "correo_asociado_al_token";
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

	@GetMapping("/usuarios/{uid}/nombre")
	public ResponseEntity<?> getNombreUsuario(@PathVariable String uid) {
		Optional<Usuario> optionalUsuario = usuarioService.findByUid(uid);

		if (optionalUsuario.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("estado", "error", "mensaje", "Usuario no encontrado"));
		}

		Usuario usuario = optionalUsuario.get();
		return ResponseEntity.ok(Map.of("nombre", usuario.getNombre()));
	}

}

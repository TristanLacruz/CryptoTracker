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

/**
 * Controlador REST para manejar las operaciones relacionadas con los usuarios.
 * Permite crear, actualizar y recuperar información de los usuarios.
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class UsuarioRestController {

	@Autowired
	private IUsuarioService usuarioService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Recupera todos los usuarios del sistema.
	 * 
	 * @return una lista de objetos Usuario
	 */
	@GetMapping("/usuarios")
	public List<Usuario> getUsuarios() {
		return usuarioService.findAll();
	}

	/**
	 * Crea un nuevo usuario en el sistema.
	 * 
	 * @param usuario el objeto Usuario a crear
	 * @throws ResponseStatusException si hay un error al crear el usuario
	 */
	@PostMapping("/usuarios")
	@ResponseStatus(HttpStatus.CREATED)
	public void createUser(@RequestBody Usuario usuario) {
		try {
			usuarioService.save(usuario);
		} catch (IllegalStateException | IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
	}

	/**
	 * Recupera el perfil del usuario autenticado.
	 * 
	 * @param auth la autenticación del usuario
	 * @return el objeto Usuario del usuario autenticado
	 */
	@GetMapping("/perfil")
	public Usuario perfilUsuario(Authentication auth) {
		return (Usuario) auth.getPrincipal();
	}

	/**
	 * Actualiza la información de un usuario existente.
	 * 
	 * @param usuario el objeto Usuario con los datos actualizados
	 * @param id      el ID del usuario a actualizar
	 * @return el usuario actualizado
	 */
	@PutMapping("/usuarios/{id}")
	public Usuario updateUsuario(@RequestBody Usuario usuario, @PathVariable String id) {
		return usuarioService.update(usuario, id);
	}

	/**
	 * Recupera la contraseña del usuario enviando un token de reseteo.
	 * 
	 * @param payload contiene el email del usuario
	 * @return un token de reseteo si el usuario existe, o un mensaje de error si no
	 *         se encuentra
	 */
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

	/**
	 * Resetea la contraseña del usuario utilizando un token de reseteo.
	 * 
	 * @param payload contiene el token de reseteo y la nueva contraseña
	 * @return una respuesta indicando el éxito o error de la operación
	 */
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

	/**
	 * Obtiene el usuario actual autenticado.
	 * 
	 * @param authentication la autenticación del usuario
	 * @return el usuario actual o un mensaje de error si no se encuentra
	 */
	@GetMapping("/usuarios/me")
	public ResponseEntity<?> getUsuarioActual(Authentication authentication) {
		String uid = (String) authentication.getPrincipal();
		Optional<Usuario> optionalUsuario = usuarioService.findByUid(uid);

		if (optionalUsuario.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
		}

		return ResponseEntity.ok(optionalUsuario.get());
	}

	/**
	 * Obtiene el nombre del usuario por su UID.
	 * 
	 * @param uid el UID del usuario
	 * @return el nombre del usuario o un mensaje de error si no se encuentra
	 */
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

package com.tracker.backend.mvc.model.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.tracker.backend.mvc.model.dao.IUsuarioDAO;
import com.tracker.backend.mvc.model.entity.Usuario;
import com.tracker.backend.mvc.model.exceptions.UsuarioNoEncontradoException;
import com.tracker.backend.mvc.model.services.IPortafolioService;
import com.tracker.backend.mvc.model.services.IUsuarioService;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Implementación del servicio de usuarios.
 * Proporciona métodos para manejar usuarios, incluyendo creación, actualización y búsqueda.
 */
@Service
public class UsuarioServiceImpl implements IUsuarioService {

	@Autowired
	private IUsuarioDAO usuarioDAO;

	@Autowired
	private IPortafolioService portafolioService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/*
	 * Método para obtener todos los usuarios.
	 */
	@Override
	public List<Usuario> findAll() {
		return (List<Usuario>) usuarioDAO.findAll();
	}

	/*
	 * Método para guardar un usuario.
	 * Si el email es nulo o vacío, lanza IllegalArgumentException.
	 * Si el email ya existe, lanza IllegalStateException.
	 * Si el usuario no existe en Firebase, lo crea.
	 */
	@Override
	public void save(Usuario u) {
		if (u.getEmail() == null || u.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentException("EMAIL_INVALID");
		}

		if (existsByEmail(u.getEmail())) {
			throw new IllegalStateException("EMAIL_EXISTS");
		}

		try {
			UserRecord firebaseUser;
			try {
				firebaseUser = FirebaseAuth.getInstance().getUserByEmail(u.getEmail());
			} catch (FirebaseAuthException ex) {
				if (ex.getAuthErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
					if (u.getContrasena() == null || u.getContrasena().isBlank()) {
						throw new IllegalArgumentException("rawPassword cannot be null");
					}

					if (u.getContrasena().length() < 6) {
						throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
					}

					UserRecord.CreateRequest request = new UserRecord.CreateRequest().setEmail(u.getEmail())
							.setPassword(u.getContrasena());

					firebaseUser = FirebaseAuth.getInstance().createUser(request);
				} else {
					throw new RuntimeException("Error al comprobar usuario en Firebase: " + ex.getMessage(), ex);
				}
			}

			u.setUid(firebaseUser.getUid());

			if (!usuarioDAO.findByUid(u.getUid()).isPresent()) {
				if (u.getContrasena() != null && !u.getContrasena().isBlank()) {
					u.setContrasena(passwordEncoder.encode(u.getContrasena()));
				} else {
					u.setContrasena("firebase");
				}
				portafolioService.getPortafolioDeUsuarioId(u.getUid()); 
				usuarioDAO.save(u);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error al guardar el usuario: " + e.getMessage(), e);
		}
	}

	/*
	 * Método para buscar un usuario por su ID.
	 * Si no se encuentra, lanza UsuarioNoEncontradoException.
	 */
	@Override
	public Optional<Usuario> findById(String id) {
		return usuarioDAO.findById(id);
	}

	/*
	 * Método para buscar un usuario por su UID de Firebase.
	 */
	public Usuario findByFirebaseUid(String uid) {
		return usuarioDAO.findByUid(uid)
				.orElseThrow(() -> new UsuarioNoEncontradoException("Usuario " + uid + " no encontrado"));
	}

	/**
	 * Método para eliminar un usuario.
	 */	
	@Override
	public void delete(Usuario u) {
		usuarioDAO.delete(u);
	}

	/*
	 * Método para actualizar un usuario.
	 * Busca el usuario por ID, si no se encuentra, lanza UsuarioNoEncontradoException.
	 */
	@Override
	public Usuario update(Usuario u, String id) {
		Usuario usuarioActual = this.findById(id).orElseThrow(() -> new UsuarioNoEncontradoException(id));

		usuarioActual.setNombreUsuario(u.getNombreUsuario());
		usuarioActual.setEmail(u.getEmail());
		usuarioActual.setNombre(u.getNombre());
		usuarioActual.setApellido(u.getApellido());
		usuarioActual.setContrasena(u.getContrasena());
		usuarioActual.setRol(u.getRol());
		usuarioActual.setCreadoEl(u.getCreadoEl());
		usuarioActual.setActualizadoEl(u.getActualizadoEl());

		this.save(usuarioActual);
		return usuarioActual;
	}

	/*
	 * Método para buscar un usuario por su nombre de usuario.
	 */
	@Override
	public Usuario findByNombreUsuario(String nombreUsuario) {
		return usuarioDAO.findByNombreUsuario(nombreUsuario)
				.orElseThrow(() -> new UsuarioNoEncontradoException(nombreUsuario));
	}

	/*
	 * Método para cargar un usuario por su nombre de usuario.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = findByNombreUsuario(username);
		return User.builder().username(usuario.getNombreUsuario()).password(usuario.getContrasena())
				.roles(usuario.getRol()) 
				.build();
	}

	/*
	 * Método para obtener o crear un usuario por su UID de Firebase.
	 */
	public Usuario getOrCreateByUid(String firebaseUid) {
		return usuarioDAO.findByUid(firebaseUid).orElseGet(() -> {
			Usuario nuevo = new Usuario();
			nuevo.setUid(firebaseUid);
			nuevo.setNombreUsuario("user_" + firebaseUid.substring(0, 6)); 
			nuevo.setRol("USER");
			nuevo.setContrasena(passwordEncoder.encode("firebase_default"));
			nuevo.setCreadoEl(LocalDateTime.now());
			nuevo.setActualizadoEl(LocalDateTime.now());
			return usuarioDAO.save(nuevo);
		});
	}

	/*
	 * Método para buscar un usuario por su email.
	 */
	@Override
	public Optional<Usuario> findByEmail(String email) {
		return Optional.empty();
	}

	/*
	 * Método para buscar un usuario por su UID de Firebase.
	 */
	@Override
	public Optional<Usuario> findByUid(String uid) {
		return usuarioDAO.findByUid(uid);
	}

	/*
	 * Método para verificar si un usuario existe por su email.
	 */
	@Override
	public boolean existsByEmail(String email) {
		return usuarioDAO.existsByEmail(email);
	}

}

package com.tracker.backend.mvc.model.services.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
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

@Service
public class UsuarioServiceImpl implements IUsuarioService {

	@Autowired
	private IUsuarioDAO usuarioDAO;

	@Autowired
	private IPortafolioService portafolioService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public List<Usuario> findAll() {
		return (List<Usuario>) usuarioDAO.findAll();
	}

	@Override
	public void save(Usuario u) {
		if (u.getEmail() == null || u.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentException("EMAIL_INVALID");
		}

		if (existsByEmail(u.getEmail())) {
			throw new IllegalStateException("EMAIL_EXISTS");
		}

		try {
			// 🔍 1. Comprobar si ya existe en Firebase
			UserRecord firebaseUser;
			try {
				firebaseUser = FirebaseAuth.getInstance().getUserByEmail(u.getEmail());
			} catch (FirebaseAuthException ex) {
				if (ex.getAuthErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
					// ✅ Crear nuevo usuario en Firebase solo si se pasa una contraseña
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

			// 🔐 2. Asignar UID (si viene de Firebase también)
			u.setUid(firebaseUser.getUid());

			// 🧠 3. Insertar en Mongo solo si no existe
			if (!usuarioDAO.findByUid(u.getUid()).isPresent()) {
				// ⚠️ Evita codificar contraseña si viene vacío desde frontend con Firebase
				if (u.getContrasena() != null && !u.getContrasena().isBlank()) {
					u.setContrasena(passwordEncoder.encode(u.getContrasena()));
				} else {
					u.setContrasena("firebase"); // dummy password
				}
				portafolioService.getPortafolioDeUsuarioId(u.getUid()); // ya lo crea con 10.000 si no existe
				usuarioDAO.save(u);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error al guardar el usuario: " + e.getMessage(), e);
		}
	}

	@Override
	public Optional<Usuario> findById(String id) {
		return usuarioDAO.findById(id);
	}

	public Usuario findByFirebaseUid(String uid) {
		return usuarioDAO.findByUid(uid)
				.orElseThrow(() -> new UsuarioNoEncontradoException("Usuario " + uid + " no encontrado"));
	}

	@Override
	public void delete(Usuario u) {
		usuarioDAO.delete(u);
	}

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

	@Override
	public Usuario findByNombreUsuario(String nombreUsuario) {
		return usuarioDAO.findByNombreUsuario(nombreUsuario)
				.orElseThrow(() -> new UsuarioNoEncontradoException(nombreUsuario));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = findByNombreUsuario(username);
		return User.builder().username(usuario.getNombreUsuario()).password(usuario.getContrasena())
				.roles(usuario.getRol()) // asegúrate que `getRol()` devuelve el rol en formato correcto (ej: "USER")
				.build();
	}

	public Usuario getOrCreateByUid(String firebaseUid) {
		return usuarioDAO.findByUid(firebaseUid).orElseGet(() -> {
			Usuario nuevo = new Usuario();
			nuevo.setUid(firebaseUid);
			// Asigna un nombre de usuario predeterminado, por ejemplo:
			nuevo.setNombreUsuario("user_" + firebaseUid.substring(0, 6)); // ejemplo
			nuevo.setRol("USER");
			// Puedes asignar una contraseña predeterminada cifrada (no se usará para login,
			// ya que se autentica en Firebase)
			nuevo.setContrasena(passwordEncoder.encode("firebase_default"));
			nuevo.setCreadoEl(LocalDateTime.now());
			nuevo.setActualizadoEl(LocalDateTime.now());
			return usuarioDAO.save(nuevo);
		});
	}

	@Override
	public Optional<Usuario> findByEmail(String email) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<Usuario> findByUid(String uid) {
		return usuarioDAO.findByUid(uid);
	}

	@Override
	public boolean existsByEmail(String email) {
		return usuarioDAO.existsByEmail(email);
	}

}

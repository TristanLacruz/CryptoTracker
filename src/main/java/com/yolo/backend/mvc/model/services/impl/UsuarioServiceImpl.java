package com.yolo.backend.mvc.model.services.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.yolo.backend.mvc.model.dao.IUsuarioDAO;
import com.yolo.backend.mvc.model.entity.Usuario;
import com.yolo.backend.mvc.model.exceptions.UsuarioNoEncontradoException;
import com.yolo.backend.mvc.model.services.IUsuarioService;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

	@Autowired
	private IUsuarioDAO usuarioDAO;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	    
	@Override
	public List <Usuario> findAll(){
		return (List<Usuario>)usuarioDAO.findAll();
	}

	@Override
	public void save(Usuario u) {
	    try {
	        // 1. Crear usuario en Firebase Authentication
	        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
	            .setEmail(u.getEmail())
	            .setPassword(u.getContrasena());

	        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
	        u.setUid(userRecord.getUid()); // asignar UID de Firebase

	    } catch (FirebaseAuthException e) {
	        throw new RuntimeException("Error al crear usuario en Firebase: " + e.getMessage());
	    }

	    // 2. Cifrar la contraseña antes de guardar en MongoDB
	    u.setContrasena(passwordEncoder.encode(u.getContrasena()));

	    // 3. Guardar el usuario en MongoDB
	    usuarioDAO.save(u);
	}

	@Override
	public Usuario findById(String id) {
		return usuarioDAO.findById(id)
				.orElseThrow(() -> new UsuarioNoEncontradoException(id));
	}

	@Override
	public void delete(Usuario u) {
		usuarioDAO.delete(u);
	}

	@Override
	public Usuario update(Usuario u, String id) {
		Usuario usuarioActual = this.findById(id);
		usuarioActual.setNombreUsuario(u.getNombreUsuario());
		usuarioActual.setEmail(u.getEmail());
		usuarioActual.setNombre(u.getNombre());
		usuarioActual.setApellido(u.getApellido());
		usuarioActual.setContrasena(u.getContrasena());
		usuarioActual.setRol(u.getRol());
		usuarioActual.setIdChatTelegram(u.getIdChatTelegram());
		usuarioActual.setSaldo(u.getSaldo());
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
	    return User.builder()
	            .username(usuario.getNombreUsuario())
	            .password(usuario.getContrasena())
	            .roles(usuario.getRol()) // asegúrate que `getRol()` devuelve el rol en formato correcto (ej: "USER")
	            .build();
	}
	
	public Usuario getOrCreateByUid(String firebaseUid) {
	    return usuarioDAO.findByUid(firebaseUid)
	            .orElseGet(() -> {
	                Usuario nuevo = new Usuario();
	                nuevo.setUid(firebaseUid);
	                // Asigna un nombre de usuario predeterminado, por ejemplo:
	                nuevo.setNombreUsuario("user_" + firebaseUid.substring(0, 6)); // ejemplo
	                nuevo.setRol("USER");
	                // Puedes asignar una contraseña predeterminada cifrada (no se usará para login, ya que se autentica en Firebase)
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


	
	
}

package com.tracker.backend.mvc.model.services;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.tracker.backend.mvc.model.entity.Usuario;

public interface IUsuarioService extends UserDetailsService {

	public List<Usuario> findAll();

	public void save(Usuario u);

	public Usuario findById(String id);

	public void delete(Usuario u);

	public Usuario update(Usuario u, String id);

	public Usuario findByNombreUsuario(String nombreUsuario);

	Optional<Usuario> findByUid(String uid);

	public Usuario getOrCreateByUid(String firebaseUid);

	@Override
	org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username);

    public Optional<Usuario> findByEmail(String email);
}

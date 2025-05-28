package com.tracker.backend.mvc.model.services;

import java.util.List;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.tracker.backend.mvc.model.entity.Usuario;

/**
 * Interfaz para el servicio de usuarios.
 * Proporciona métodos para manejar usuarios, incluyendo búsqueda, guardado, actualización y eliminación.
 */
public interface IUsuarioService extends UserDetailsService {

	/**
	 * Método para obtener todos los usuarios.
	 * @return Lista de todos los usuarios.
	 */
	public List<Usuario> findAll();

	/**
	 * Método para guardar un usuario.
	 * @param u Usuario a guardar.
	 */
	public void save(Usuario u);

	/**
	 * Método para buscar un usuario por su ID.
	 * @param id ID del usuario a buscar.
	 * @return Usuario encontrado.
	 */
	public Optional<Usuario> findById(String id);

	/**
	 * Método para eliminar un usuario.
	 * @param u Usuario a eliminar.
	 */
	public void delete(Usuario u);

	/**
	 * Método para actualizar un usuario.
	 * @param u Usuario a actualizar.
	 * @param id ID del usuario a actualizar.
	 * @return Usuario actualizado.
	 */
	public Usuario update(Usuario u, String id);

	/**
	 * Método para buscar un usuario por su nombre de usuario.
	 * @param nombreUsuario Nombre de usuario a buscar.
	 * @return Usuario encontrado.
	 */
	public Usuario findByNombreUsuario(String nombreUsuario);

	/**
	 * Método para buscar un usuario por su UID de Firebase.
	 * @param uid UID de Firebase del usuario a buscar.
	 * @return Usuario encontrado.
	 */
	Optional<Usuario> findByUid(String uid);

	/**
	 * Método para obtener o crear un usuario por su UID de Firebase.
	 * Si el usuario no existe, lo crea.
	 * @param firebaseUid UID de Firebase del usuario.
	 * @return Usuario encontrado o creado.
	 */
	public Usuario getOrCreateByUid(String firebaseUid);

	/**
	 * Método para cargar un usuario por su nombre de usuario.
	 * @param username Nombre de usuario a cargar.
	 * @return Detalles del usuario cargado.
	 */
	@Override
	org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username); 
 
	/**
	 * Método para buscar un usuario por su email.
	 * @param email Email del usuario a buscar.
	 * @return Usuario encontrado.
	 */
    public Optional<Usuario> findByEmail(String email);
    
	/**
	 * Método para verificar si un usuario existe por su email.
	 * @param email Email del usuario a verificar.
	 * @return true si el usuario existe, false en caso contrario.
	 */
    boolean existsByEmail(String email);

}

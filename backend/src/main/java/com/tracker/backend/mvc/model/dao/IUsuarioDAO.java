package com.tracker.backend.mvc.model.dao;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.tracker.backend.mvc.model.entity.Usuario;

/**
 * Interfaz para el acceso a datos de usuarios.
 * Extiende de MongoRepository para proporcionar operaciones CRUD básicas.
 */
public interface IUsuarioDAO extends MongoRepository<Usuario, String>{

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param nombreUsuario el nombre de usuario
     * @return un Optional que contiene el usuario si se encuentra, o vacío si no
     */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    /**
     * Busca un usuario por su UID.
     *
     * @param uid el UID del usuario
     * @return un Optional que contiene el usuario si se encuentra, o vacío si no
     */
    Optional<Usuario> findByUid(String uid);
    
    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email el correo electrónico del usuario
     * @return un Optional que contiene el usuario si se encuentra, o vacío si no
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el correo electrónico proporcionado.
     *
     * @param email el correo electrónico a verificar
     * @return true si existe un usuario con ese correo, false en caso contrario
     */
    boolean existsByEmail(String email);

}

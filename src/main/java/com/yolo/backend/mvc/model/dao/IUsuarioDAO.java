package com.yolo.backend.mvc.model.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.yolo.backend.mvc.model.entity.Usuario;

public interface IUsuarioDAO extends MongoRepository<Usuario, String>{

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    Optional<Usuario> findByUid(String uid);

    Optional<Usuario> findByEmail(String email);

}

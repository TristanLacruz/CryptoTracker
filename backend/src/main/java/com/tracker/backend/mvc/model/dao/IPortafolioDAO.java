package com.tracker.backend.mvc.model.dao;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.tracker.backend.mvc.model.entity.Portafolio;

/**
 * Interfaz para el acceso a datos de portafolios.
 * Extiende de MongoRepository para proporcionar operaciones CRUD básicas.
 */
public interface IPortafolioDAO extends MongoRepository<Portafolio, String> {

    /**
     * Busca un portafolio por el ID del usuario.
     *
     * @param userId el ID del usuario
     * @return un Optional que contiene el portafolio si se encuentra, o vacío si no
     */
    Optional<Portafolio> findByUsuarioId(String userId);

}

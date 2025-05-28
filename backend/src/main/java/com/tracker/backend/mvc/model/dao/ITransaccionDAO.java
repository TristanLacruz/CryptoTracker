package com.tracker.backend.mvc.model.dao;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.tracker.backend.mvc.model.entity.Transaccion;
import com.tracker.backend.mvc.model.entity.TransactionType;

/**
 * Interfaz para manejar las operaciones de acceso a datos de las transacciones.
 * Extiende de MongoRepository para proporcionar métodos CRUD básicos.
 */
public interface ITransaccionDAO extends MongoRepository<Transaccion, String> {

	/**
	 * Busca transacciones por el ID del usuario y las ordena por fecha de transacción de forma descendente.
	 *
	 * @param usuarioId el ID del usuario
	 * @return una lista de transacciones ordenadas por fecha de transacción de forma descendente
	 */
	List<Transaccion> findByUsuarioIdOrderByFechaTransaccionDesc(String usuarioId);

	/**
	 * Busca transacciones por el ID del usuario y el tipo de transacción.
	 *
	 * @param usuarioId el ID del usuario
	 * @param tipo el tipo de transacción (COMPRA o VENTA)
	 * @return una lista de transacciones que coinciden con el usuario y el tipo de transacción
	 */
	List<Transaccion> findByUsuarioIdAndTipoTransaccion(String usuarioId, TransactionType tipo);

	/**
	 * Busca transacciones por el ID del usuario y las ordena por fecha de transacción de forma ascendente.
	 *
	 * @param usuarioId el ID del usuario
	 * @return una lista de transacciones ordenadas por fecha de transacción de forma ascendente
	 */
	List<Transaccion> findByUsuarioIdOrderByFechaTransaccionAsc(String usuarioId);

}

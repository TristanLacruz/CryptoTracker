package com.yolo.backend.mvc.model.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.yolo.backend.mvc.model.entity.Transaccion;
import com.yolo.backend.mvc.model.entity.TransactionType;

public interface ITransaccionDAO extends MongoRepository<Transaccion, String> {

	List<Transaccion> findByUsuarioIdOrderByFechaTransaccionDesc(String usuarioId);
	List<Transaccion> findByUsuarioIdAndTipoTransaccion(String usuarioId, TransactionType tipo);
	List<Transaccion> findByUsuarioIdOrderByFechaTransaccionAsc(String usuarioId);

}

package com.tracker.backend.mvc.model.services;

import java.util.List;
import com.tracker.backend.mvc.model.entity.Transaccion;

/*
 * Interfaz para el servicio de transacciones.
 * Proporciona métodos para manejar transacciones de compra y venta de criptomonedas.
 */
public interface ITransaccionService {

	/**
	 * Método para obtener todas las transacciones.
	 * @return Lista de todas las transacciones.
	 */
	public List<Transaccion> findAll();

	/**
	 * Método para guardar una transacción.
	 * @param t Transacción a guardar.
	 */
	public void save(Transaccion t);

	/**
	 * Método para buscar una transacción por su ID.
	 * @param id ID de la transacción a buscar.
	 * @return Transacción encontrada.
	 */
	public Transaccion findById(String id);

	/**
	 * Método para eliminar una transacción.
	 * @param t Transacción a eliminar.
	 */
	public void delete(Transaccion t);

	/**
	 * Método para actualizar una transacción.
	 * @param t Transacción a actualizar.
	 * @param id ID de la transacción a actualizar.
	 * @return Transacción actualizada.
	 */
	public Transaccion update(Transaccion t, String id);

	/**
	 * Método para obtener el total invertido por un usuario.
	 * @param usuarioId ID del usuario.
	 * @return Total invertido por el usuario.
	 */
	public double getTotalInvertido(String usuarioId);

	/**
	 * Método para comprar criptomonedas.
	 * @param uid ID del usuario.
	 * @param simbolo Símbolo de la criptomoneda.
	 * @param nombreCrypto Nombre de la criptomoneda.
	 * @param cantidadCrypto Cantidad de criptomonedas a comprar.
	 * @param precioUnitario Precio unitario de la criptomoneda.
	 * @return Transacción de compra realizada.
	 */
	Transaccion comprarCrypto(String uid, String simbolo, String nombreCrypto, double cantidadCrypto,
			double precioUnitario);

	/**
	 * Método para vender criptomonedas.
	 * @param usuarioId ID del usuario.
	 * @param simbolo Símbolo de la criptomoneda.
	 * @param nombreCrypto Nombre de la criptomoneda.
	 * @param cantidadCrypto Cantidad de criptomonedas a vender.
	 * @param precioUnitario Precio unitario de la criptomoneda.
	 * @return Transacción de venta realizada.
	 */
	Transaccion venderCrypto(String usuarioId, String simbolo, String nombreCrypto, double cantidadCrypto,
			double precioUnitario);

	/**
	 * Método para buscar transacciones por el ID del usuario.
	 * @param usuarioId ID del usuario.
	 * @return Lista de transacciones del usuario.
	 */
	List<Transaccion> findByUsuarioId(String usuarioId);

}

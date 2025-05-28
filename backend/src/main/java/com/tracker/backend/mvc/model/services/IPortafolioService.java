package com.tracker.backend.mvc.model.services;

import java.util.List;
import com.tracker.common.dto.EvolucionCompletaDTO;
import com.tracker.common.dto.RendimientoDiarioDTO;
import com.tracker.common.dto.ValorDiarioDTO;
import com.tracker.backend.mvc.model.entity.Portafolio;
import com.tracker.backend.mvc.model.entity.Transaccion;

/**
 * Interfaz para el servicio de portafolios.
 * Proporciona métodos para manejar portafolios, incluyendo búsqueda, guardado, actualización y eliminación.
 */
public interface IPortafolioService {

	/**
	 * Método para obtener todos los portafolios.
	 * @return Lista de todos los portafolios.
	 */
	public List<Portafolio> findAll();

	/**
	 * Método para guardar un portafolio.
	 * @param p Portafolio a guardar.
	 */
	public void save(Portafolio p);

	/**
	 * Método para buscar un portafolio por su ID.
	 * @param id ID del portafolio a buscar.
	 * @return Portafolio encontrado.
	 */
	public Portafolio findById(String id);

	/**
	 * Método para eliminar un portafolio.
	 * @param p Portafolio a eliminar.
	 */
	public void delete(Portafolio p);

	/**
	 * Método para actualizar un portafolio.
	 * @param p Portafolio a actualizar.
	 * @param id ID del portafolio a actualizar.
	 * @return Portafolio actualizado.
	 */
	public Portafolio update(Portafolio p, String id);

	/**
	 * Método para añadir una criptomoneda al portafolio de un usuario.
	 * @param usuarioId ID del usuario.
	 * @param simbolo Símbolo de la criptomoneda.
	 * @param cantidad Cantidad de criptomonedas a añadir.
	 */
	void anadirCrypto(String usuarioId, String simbolo, double cantidad);

	/**
	 * Método para eliminar una criptomoneda del portafolio de un usuario.
	 * @param usuarioId ID del usuario.
	 * @param simbolo Símbolo de la criptomoneda.
	 * @param cantidad Cantidad de criptomonedas a eliminar.
	 */
	void eliminarCrypto(String usuarioId, String simbolo, double cantidad);

	/**
	 * Método para verificar si un usuario tiene suficiente cantidad de una criptomoneda.
	 * @param usuarioId ID del usuario.
	 * @param simbolo Símbolo de la criptomoneda.
	 * @param cantidad Cantidad de criptomonedas a verificar.
	 * @return true si tiene suficiente, false en caso contrario.
	 */
	boolean tieneSuficienteCrypto(String usuarioId, String simbolo, double cantidad);

	/**
	 * Método para obtener el portafolio de un usuario por su ID.
	 * @param usuarioId ID del usuario.
	 * @return Portafolio del usuario.
	 */
	Portafolio getPortafolioDeUsuarioId(String usuarioId);

	/**
	 * Método para actualizar el portafolio después de una compra.
	 * @param usuarioId ID del usuario.
	 * @param cryptoId ID de la criptomoneda.
	 * @param cantidad Cantidad comprada.
	 */
	void updatePortafolioDespuesDeCompra(String usuarioId, String cryptoId, double cantidad);

	/**
	 * Método para buscar un portafolio por el ID del usuario.
	 * @param usuarioId ID del usuario.
	 * @return Portafolio del usuario.
	 */
	Portafolio findByUsuarioId(String usuarioId);

	/**
	 * Método para actualizar el portafolio de un usuario con una transacción.
	 * @param uid UID del usuario.
	 * @param cryptoId ID de la criptomoneda.
	 * @param cantidad Cantidad de criptomonedas.
	 * @param precioCompra Precio de compra de la criptomoneda.
	 */
	void actualizarPortafolio(String uid, String cryptoId, double cantidad, double precioCompra);

	/**
	 * Método para calcular la evolución del portafolio de un usuario.
	 * @param usuarioId ID del usuario.
	 * @return Lista de valores diarios del portafolio.
	 */
	List<ValorDiarioDTO> calcularEvolucion(String usuarioId);

	/**
	 * Método para calcular la evolución completa del portafolio de un usuario.
	 * @param usuarioId ID del usuario.
	 * @return Lista de evoluciones completas del portafolio.
	 */
	List<EvolucionCompletaDTO> calcularEvolucionCompleta(String usuarioId);

	/**
	 * Método para calcular el rendimiento diario del portafolio de un usuario.
	 * @param usuarioId ID del usuario.
	 * @return Lista de rendimientos diarios del portafolio.
	 */
	List<RendimientoDiarioDTO> calcularRendimiento(String usuarioId);

	/**
	 * Método para actualizar el portafolio con una transacción.
	 * @param transaccion Transacción a procesar.
	 */
	void actualizarPortafolioConTransaccion(Transaccion transaccion);

	/**
	 * Método para obtener el saldo total del portafolio de un usuario.
	 * @param usuarioId ID del usuario.
	 * @return Saldo total del portafolio.
	 */
	double obtenerSaldo(String usuarioId);
}

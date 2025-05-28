package com.tracker.backend.mvc.model.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.tracker.backend.mvc.model.entity.Criptomoneda;
import com.tracker.common.dto.CryptoMarketDTO;

/*
 * Interfaz para el servicio de criptomonedas.
 * Proporciona métodos para manejar criptomonedas, incluyendo búsqueda, guardado, actualización y eliminación.
 */
public interface ICriptomonedaService {

	/**
	 * Método para obtener todas las criptomonedas.
	 * @return Lista de todas las criptomonedas.
	 */
	public List<Criptomoneda> findAll();

	/**
	 * Método para guardar una criptomoneda.
	 * @param c Criptomoneda a guardar.
	 */
	public void save(Criptomoneda c);

	/**
	 * Método para buscar una criptomoneda por su ID.
	 * @param id ID de la criptomoneda a buscar.
	 * @return Criptomoneda encontrada.
	 */
	public Criptomoneda findById(String id);

	/**
	 * Método para eliminar una criptomoneda.
	 * @param c Criptomoneda a eliminar.
	 */
	public void delete(Criptomoneda c);

	/**
	 * Método para actualizar una criptomoneda.
	 * @param c Criptomoneda a actualizar.
	 * @param id ID de la criptomoneda a actualizar.
	 * @return Criptomoneda actualizada.
	 */
	public Criptomoneda update(Criptomoneda c, String id);

	/**
	 * Método para buscar una criptomoneda por su símbolo.
	 * @param symbol Símbolo de la criptomoneda a buscar.
	 * @return Criptomoneda encontrada.
	 */
	double getPrecioActual(String symbol);

	/**
	 * Método para obtener información de una criptomoneda por su símbolo.
	 * @param symbol Símbolo de la criptomoneda.
	 * @return Mapa con información de la criptomoneda.
	 */
	Map<String, Object> getCryptoInfo(String symbol);

	/**
	 * Método para obtener datos del mercado de criptomonedas.
	 * @return Lista de datos del mercado de criptomonedas.
	 */
	List<CryptoMarketDTO> getMarketData();

	/**
	 * Método para obtener el precio histórico de una criptomoneda.
	 * @param cryptoId ID de la criptomoneda.
	 * @return Lista de listas con precios históricos.
	 */
	List<List<Double>> getHistoricalPrices(String cryptoId);

	/**
	 * Método para obtener el RSI histórico de una criptomoneda.
	 * @param cryptoId ID de la criptomoneda.
	 * @return Lista de valores RSI históricos.
	 */
	List<Double> getHistoricalRSI(String cryptoId);

	/**
	 * Método para obtener el precio de una criptomoneda en una fecha específica.
	 * @param cryptoId ID de la criptomoneda.
	 * @param fecha Fecha para la cual se desea obtener el precio.
	 * @return Precio de la criptomoneda en la fecha especificada.
	 */
	double getPrecioEnFecha(String cryptoId, LocalDate fecha);

	/**
	 * Método para calcular el valor actual de las criptomonedas de un usuario.
	 * @param usuarioId ID del usuario.
	 * @return Valor total en criptomonedas del usuario.
	 */
	double calcularValorActualEnCriptos(String usuarioId);

}

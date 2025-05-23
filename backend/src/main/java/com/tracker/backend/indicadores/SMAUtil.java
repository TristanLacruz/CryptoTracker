package com.tracker.backend.indicadores;

import java.util.ArrayList;
import java.util.List;

public class SMAUtil {

	/**
	 * Calcula la media móvil simple (SMA) de una lista de precios.
	 *
	 * @param precios  la lista de precios
	 * @param periodos el número de periodos para calcular la SMA
	 * @return una lista con los valores de SMA
	 */
	public static List<Double> calcularSMA(List<Double> precios, int periodos) {
		if (precios == null || precios.size() < periodos) {
		    throw new IllegalArgumentException("La lista debe tener al menos tantos valores como el periodo.");
		}

		List<Double> sma = new ArrayList<>();
	    for (int i = 0; i <= precios.size() - periodos; i++) {
	        double suma = 0;
	        for (int j = i; j < i + periodos; j++) {
	            suma += precios.get(j);
	        }
	        sma.add(suma / periodos);
	    }
	    return sma;
	}

}

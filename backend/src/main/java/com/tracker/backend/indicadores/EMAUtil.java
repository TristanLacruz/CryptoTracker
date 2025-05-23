package com.tracker.backend.indicadores;

import java.util.ArrayList;
import java.util.List;

public class EMAUtil {

	/**
	 * Calcula la media móvil exponencial (EMA) de una lista de precios.
	 *
	 * @param precios  la lista de precios
	 * @param periodos el número de periodos para calcular la EMA
	 * @return una lista con los valores de EMA
	 */
	public static List<Double> calcularEMA(List<Double> precios, int periodos) {
		if (precios == null || precios.size() < periodos) {
		    throw new IllegalArgumentException("La lista debe tener al menos tantos valores como el periodo.");
		}

		List<Double> ema = new ArrayList<>();
	    if (precios.size() < periodos) return ema;

		double sma = 0;
	    for (int i = 0; i < periodos; i++) {
	        sma += precios.get(i);
	    }
	    sma /= periodos;
	    ema.add(sma); // Primer valor de EMA

	    double k = 2.0 / (periodos + 1);

	    for (int i = periodos; i < precios.size(); i++) {
	        double emaActual = precios.get(i) * k + ema.get(ema.size() - 1) * (1 - k);
	        ema.add(emaActual);
	    }

	    return ema;
	}

}

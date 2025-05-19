package com.tracker.backend.indicadores;

import java.util.ArrayList;
import java.util.List;

public class EMAUtil {

	public static List<Double> calcularEMA(List<Double> precios, int periodos) {
	    List<Double> ema = new ArrayList<>();
	    if (precios.size() < periodos) return ema;

	    // Calcular la SMA inicial para los primeros 'periodos'
	    double sma = 0;
	    for (int i = 0; i < periodos; i++) {
	        sma += precios.get(i);
	    }
	    sma /= periodos;
	    ema.add(sma); // Primer valor de EMA

	    double k = 2.0 / (periodos + 1);

	    // Calcular el resto de EMA
	    for (int i = periodos; i < precios.size(); i++) {
	        double emaActual = precios.get(i) * k + ema.get(ema.size() - 1) * (1 - k);
	        ema.add(emaActual);
	    }

	    return ema;
	}

}

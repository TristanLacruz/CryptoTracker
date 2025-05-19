package com.tracker.backend.indicadores;

import java.util.ArrayList;
import java.util.List;

public class SMAUtil {

	public static List<Double> calcularSMA(List<Double> precios, int periodos) {
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

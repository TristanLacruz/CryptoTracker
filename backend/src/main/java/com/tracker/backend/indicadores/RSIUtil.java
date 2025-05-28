package com.tracker.backend.indicadores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RSIUtil {

	/**
	 * Calcula el Índice de Fuerza Relativa (RSI) para una lista de precios.
	 *
	 * @param precios la lista de precios
	 * @return el valor del RSI
	 */
	public static double calcularRSI(List<Double> precios) {
	    if (precios.size() < 15) {
	        throw new IllegalArgumentException("Se necesitan al menos 15 precios para calcular RSI.");
	    }

	    double gananciaPromedio = 0;
	    double perdidaPromedio = 0;

	    for (int i = 1; i <= 14; i++) {
	        double cambio = precios.get(i) - precios.get(i - 1);
	        if (cambio > 0) {
	            gananciaPromedio += cambio;
	        } else {
	            perdidaPromedio += -cambio;
	        }
	    }

	    gananciaPromedio /= 14.0;
	    perdidaPromedio /= 14.0;

	    if (perdidaPromedio == 0) return 100.0;

	    double rs = gananciaPromedio / perdidaPromedio;
	    
	    //System.out.println("RS: " + rs);
	    return 100 - (100 / (1 + rs));
	}

	/**
	 * Calcula el Índice de Fuerza Relativa (RSI) para una lista de precios con un período específico.
	 *
	 * @param prices la lista de precios
	 * @param period el período para el cálculo del RSI
	 * @return una lista de valores RSI calculados
	 */
	public static List<Double> calculateRSIList(List<Double> prices, int period) {
	    List<Double> rsiList = new ArrayList<>();
	    if (prices.size() < period + 1) return rsiList;

	    for (int i = 0; i <= prices.size() - period - 1; i++) {
	        List<Double> sublist = prices.subList(i, i + period + 1);
	        double rsi = calcularRSI(sublist);
	        rsiList.add(rsi);
	    }
	    return rsiList;
	}







}

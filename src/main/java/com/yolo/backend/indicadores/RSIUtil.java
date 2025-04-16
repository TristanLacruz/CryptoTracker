package com.yolo.backend.indicadores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RSIUtil {

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

		if (perdidaPromedio == 0)
			return 100.0;

		double rs = gananciaPromedio / perdidaPromedio;
		return 100 - (100 / (1 + rs));
	}

	public static List<Double> calculateRSI(List<Double> prices) {
	    int period = 14;
	    List<Double> rsi = new ArrayList<>();

	    if (prices.size() < period) return rsi; // No hay suficientes datos

	    for (int i = 0; i <= prices.size() - period; i++) {
	        double gain = 0, loss = 0;
	        for (int j = i; j < i + period - 1; j++) {
	            double diff = prices.get(j + 1) - prices.get(j);
	            if (diff >= 0) gain += diff;
	            else loss -= diff;
	        }

	        double avgGain = gain / period;
	        double avgLoss = loss / period;

	        double rs = avgLoss == 0 ? 100 : avgGain / avgLoss;
	        double rsiValue = 100 - (100 / (1 + rs));
	        rsi.add(rsiValue);
	    }

	    return rsi;
	}
	
	public static List<Double> calculateRSIList(List<Double> prices, int period) {
	    List<Double> rsiList = new ArrayList<>();
	    if (prices.size() < period + 1) return rsiList;

	    for (int i = 0; i <= prices.size() - period - 1; i++) {
	        int end = i + period + 1;
	        if (end <= prices.size()) {
	            List<Double> sublist = prices.subList(i, end);
	            double rsi = calcularRSI(sublist); // tu método actual
	            rsiList.add(rsi);
	        }
	    }
	    return rsiList;
	}







}

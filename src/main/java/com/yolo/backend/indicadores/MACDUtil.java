package com.yolo.backend.indicadores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MACDUtil {

	public static Map<String, List<Double>> calcularMACD(List<Double> precios) {
	    int rapida = 12;
	    int lenta = 26;
	    int señal = 9;

	    Map<String, List<Double>> resultado = new HashMap<>();

	    if (precios.size() < lenta + señal) {
	        System.err.println("❌ No hay suficientes precios para MACD.");
	        resultado.put("macd", List.of());
	        resultado.put("signal", List.of());
	        return resultado;
	    }

	    List<Double> emaRapida = EMAUtil.calcularEMA(precios, rapida);
	    List<Double> emaLenta = EMAUtil.calcularEMA(precios, lenta);

	    if (emaRapida.size() <= emaLenta.size()) {
	        System.err.println("❌ EMAs no alineadas.");
	        resultado.put("macd", List.of());
	        resultado.put("signal", List.of());
	        return resultado;
	    }

	    int offset = emaRapida.size() - emaLenta.size();
	    List<Double> macdLine = new ArrayList<>();
	    for (int i = 0; i < emaLenta.size(); i++) {
	        macdLine.add(emaRapida.get(i + offset) - emaLenta.get(i));
	    }

	    if (macdLine.size() < señal) {
	        System.err.println("❌ No hay suficientes datos para calcular la señal del MACD.");
	        resultado.put("macd", macdLine);
	        resultado.put("signal", List.of());
	        return resultado;
	    }

	    List<Double> signalLine = EMAUtil.calcularEMA(macdLine, señal);

	    resultado.put("macd", macdLine);
	    resultado.put("signal", signalLine);
	    return resultado;
	}



}

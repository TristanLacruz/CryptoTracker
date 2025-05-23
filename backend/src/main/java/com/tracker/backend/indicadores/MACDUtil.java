package com.tracker.backend.indicadores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MACDUtil {

    /**
     * Calcula el MACD (Moving Average Convergence Divergence) de una lista de precios.
     *
     * @param precios la lista de precios
     * @return un mapa con las líneas MACD y de señal
     */
    public static Map<String, List<Double>> calcularMACD(List<Double> precios) {
        if (precios == null || precios.size() < 35) { // 26 + 9 mínimo recomendado
            throw new IllegalArgumentException("Se necesitan al menos 35 precios para calcular el MACD.");
        }

        int rapida = 12;
        int lenta = 26;
        int señal = 9;

        Map<String, List<Double>> resultado = new HashMap<>();

        List<Double> emaRapida = EMAUtil.calcularEMA(precios, rapida);
        List<Double> emaLenta = EMAUtil.calcularEMA(precios, lenta);

        int offset = emaRapida.size() - emaLenta.size();
        if (offset < 0) {
            throw new IllegalStateException("La EMA rápida debe tener más valores que la lenta.");
        }

        List<Double> macdLine = new ArrayList<>();
        for (int i = 0; i < emaLenta.size(); i++) {
            macdLine.add(emaRapida.get(i + offset) - emaLenta.get(i));
        }

        if (macdLine.size() < señal) {
            throw new IllegalArgumentException("No hay suficientes valores para calcular la línea de señal.");
        }

        List<Double> signalLine = EMAUtil.calcularEMA(macdLine, señal);

        resultado.put("macd", macdLine);
        resultado.put("signal", signalLine);
        return resultado;
    }
}

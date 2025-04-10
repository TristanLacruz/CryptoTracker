package com.yolo.backend.util.indicadores;

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

        if (perdidaPromedio == 0) return 100.0;

        double rs = gananciaPromedio / perdidaPromedio;
        return 100 - (100 / (1 + rs));
    }
    
    public static List<Double> calcularRSIHistorico(List<Double> precios) {
        List<Double> rsiList = new ArrayList<>();

        if (precios.size() < 15) {
            return Collections.nCopies(precios.size(), 50.0); // Valor neutral si no hay suficientes datos
        }

        for (int i = 14; i < precios.size(); i++) {
            List<Double> sub = precios.subList(i - 14, i + 1);
            rsiList.add(calcularRSI(sub));
        }

        // Ajustamos para que la lista tenga el mismo tamaño que precios, rellenando con nulos al principio
        int padding = precios.size() - rsiList.size();
        List<Double> finalRSI = new ArrayList<>(Collections.nCopies(padding, null));
        finalRSI.addAll(rsiList);

        return finalRSI;
    }

}

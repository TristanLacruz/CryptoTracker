package com.yolo.frontend.views.graficos;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

/**
 * Combina los tres gráficos en un solo panel modular.
 */
public class PanelGraficoView extends VBox {

    public PanelGraficoView(String cryptoId) {
        setSpacing(10);
        setPadding(new Insets(10));

        GraficoPrecioView graficoPrecio = new GraficoPrecioView(cryptoId);
        GraficoRSIView graficoRSI = new GraficoRSIView(cryptoId);
        GraficoMACDView graficoMACD = new GraficoMACDView(cryptoId);

        getChildren().addAll(graficoPrecio, graficoRSI, graficoMACD);
    }
} 

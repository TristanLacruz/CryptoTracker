package com.tracker.frontend.views.graficos;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
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

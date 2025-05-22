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

//        // ⬇️ Crear texto "Día" alineado a la derecha debajo del gráfico MACD
//        Label labelDiaMACD = new Label("Días");
//        labelDiaMACD.setStyle("-fx-text-fill: #00FF00; -fx-font-family: Consolas; -fx-font-size: 10px;");
//
//        HBox diaMACDContainer = new HBox(labelDiaMACD);
//        diaMACDContainer.setAlignment(Pos.CENTER_RIGHT);
//        diaMACDContainer.setPadding(new Insets(0, 10, 0, 0));
//
//        VBox macdPanel = new VBox(5, graficoMACD, diaMACDContainer);

        getChildren().addAll(graficoPrecio, graficoRSI, graficoMACD);
    }
}

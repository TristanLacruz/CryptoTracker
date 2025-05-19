package com.tracker.frontend.views;

import com.tracker.frontend.views.graficos.PanelGraficoView;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CryptoDetailView {

    private final String cryptoId;
    private final String nombreCrypto;
    private final double precioActual;

    public CryptoDetailView(String cryptoId, String nombreCrypto, double precioActual) {
        this.cryptoId = cryptoId;
        this.nombreCrypto = nombreCrypto;
        this.precioActual = precioActual;
    }

    public void mostrar() {
        Stage stage = new Stage();
        stage.setTitle("Detalles de: " + cryptoId);

        PanelGraficoView panelGraficos = new PanelGraficoView(cryptoId);
        SimuladorOperacionView panelOperacion = new SimuladorOperacionView(cryptoId, nombreCrypto, precioActual);

        VBox layout = new VBox(10, panelGraficos, panelOperacion);
        layout.setPadding(new Insets(10));

        stage.setScene(new Scene(layout, 700, 700));
        stage.show();
    }
}

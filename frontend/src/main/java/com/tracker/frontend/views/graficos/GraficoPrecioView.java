package com.tracker.frontend.views.graficos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GraficoPrecioView extends VBox {

    private final LineChart<Number, Number> chart;
    private final NumberAxis yAxis;

    public GraficoPrecioView(String cryptoId) {
        NumberAxis xAxis = new NumberAxis();
        yAxis = new NumberAxis(); 

        chart = new LineChart<>(xAxis, yAxis);
        chart.setMinHeight(250);
        chart.setPrefHeight(300);

        this.setSpacing(10);

        Label ejeXLabel = new Label("Días");
        ejeXLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-family: Consolas; -fx-font-size: 10px;");
        HBox labelContainer = new HBox(ejeXLabel);
        labelContainer.setAlignment(Pos.CENTER_RIGHT);
        labelContainer.setPadding(new Insets(0, 10, 0, 0)); 

        this.getChildren().addAll(chart, labelContainer);

        cargarDatos(cryptoId);
    }

    private void cargarDatos(String cryptoId) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/cryptos/" + cryptoId + "/indicadores";
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.body());

                List<Double> precios = mapper.convertValue(root.get("precios"), new TypeReference<List<Double>>() {});
                List<Double> sma = mapper.convertValue(root.get("sma"), new TypeReference<List<Double>>() {});
                List<Double> ema = mapper.convertValue(root.get("ema"), new TypeReference<List<Double>>() {});

                // Calcular mínimo y máximo para eje Y
                double min = precios.stream().min(Double::compareTo).orElse(0.0);
                double max = precios.stream().max(Double::compareTo).orElse(100.0);

                XYChart.Series<Number, Number> priceSeries = new XYChart.Series<>();
                priceSeries.setName("Precio");
                for (int i = 0; i < precios.size(); i++) {
                    priceSeries.getData().add(new XYChart.Data<>(i + 1, precios.get(i)));
                }

                XYChart.Series<Number, Number> smaSeries = new XYChart.Series<>();
                smaSeries.setName("SMA");
                for (int i = 0; i < sma.size(); i++) {
                    smaSeries.getData().add(new XYChart.Data<>(i + 7, sma.get(i)));
                }

                XYChart.Series<Number, Number> emaSeries = new XYChart.Series<>();
                emaSeries.setName("EMA");
                for (int i = 0; i < ema.size(); i++) {
                    emaSeries.getData().add(new XYChart.Data<>(i + 7, ema.get(i)));
                }

                Platform.runLater(() -> {
                    yAxis.setAutoRanging(false);
                    yAxis.setLowerBound(min * 0.98);
                    yAxis.setUpperBound(max * 1.02);
                    yAxis.setTickUnit((max - min) / 10);

                    chart.getData().clear();
                    chart.getData().addAll(priceSeries, smaSeries, emaSeries);
                   
                    double precioActual = precios.get(precios.size() - 1);
                    chart.setTitle("Precio de " + cryptoId + ": " + String.format("%.2f", precioActual) + "€");
                    
                    priceSeries.getNode().setStyle("-fx-stroke: #00FF00; -fx-stroke-width: 2.5px;"); 
                    smaSeries.getNode().setStyle("-fx-stroke: #FF4081; -fx-stroke-width: 2.5px;");
                    emaSeries.getNode().setStyle("-fx-stroke: #448AFF; -fx-stroke-width: 2.5px;"); 
                });

            } catch (Exception e) {
                System.err.println("Error al cargar gráfico de precios: " + e.getMessage());
            }
        }).start();
    }
}

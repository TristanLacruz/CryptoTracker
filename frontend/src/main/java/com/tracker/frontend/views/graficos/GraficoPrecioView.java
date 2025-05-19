package com.tracker.frontend.views.graficos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GraficoPrecioView extends VBox {

    private final LineChart<Number, Number> chart;

    public GraficoPrecioView(String cryptoId) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Día");
        yAxis.setLabel("Precio (€)");
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Histórico de Precio + SMA/EMA");
        chart.setMinHeight(250);
        chart.setPrefHeight(300);
        this.setSpacing(10);
        this.getChildren().add(chart);

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

                System.out.println("📊 Precios: " + precios);
                System.out.println("📊 SMA: " + sma);
                System.out.println("📊 EMA: " + ema);

                Platform.runLater(() -> {
                    chart.getData().clear(); // por si hay datos anteriores ocultos
                    chart.getData().addAll(priceSeries, smaSeries, emaSeries);
                    System.out.println("✅ Añadidas series al gráfico de precio");
                });


            } catch (Exception e) {
                System.err.println("❌ Error al cargar gráfico de precios: " + e.getMessage());
            }
        }).start();
    }
}

package com.tracker.frontend.views.graficos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GraficoMACDView extends LineChart<Number, Number> {

    public GraficoMACDView(String cryptoId) {
        super(new NumberAxis(), new NumberAxis());
        this.setTitle("MACD y Señal");

        NumberAxis xAxis = (NumberAxis) this.getXAxis();
        NumberAxis yAxis = (NumberAxis) this.getYAxis();
        xAxis.setLabel("Día");
        yAxis.setLabel("MACD");

        cargarDatos(cryptoId);
    }

    private void cargarDatos(String cryptoId) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/cryptos/" + cryptoId + "/indicadores";
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.body());

                List<Double> macd = mapper.convertValue(root.get("macd"), new TypeReference<List<Double>>() {});
                List<Double> signal = mapper.convertValue(root.get("signal"), new TypeReference<List<Double>>() {});


                XYChart.Series<Number, Number> macdSeries = new XYChart.Series<>();
                macdSeries.setName("MACD");

                XYChart.Series<Number, Number> signalSeries = new XYChart.Series<>();
                signalSeries.setName("Señal");

                for (int i = 0; i < macd.size(); i++) {
                    macdSeries.getData().add(new XYChart.Data<>(i + 1, macd.get(i)));
                }

                for (int i = 0; i < signal.size(); i++) {
                    signalSeries.getData().add(new XYChart.Data<>(i + 1 + (macd.size() - signal.size()), signal.get(i)));
                }

                Platform.runLater(() -> this.getData().addAll(macdSeries, signalSeries));

            } catch (Exception e) {
                System.err.println("❌ Error al cargar MACD: " + e.getMessage());
            }
        }).start();
    }
}

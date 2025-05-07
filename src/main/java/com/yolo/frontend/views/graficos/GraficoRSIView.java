package com.yolo.frontend.views.graficos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GraficoRSIView extends VBox {

    private final Label rsiLabel = new Label("RSI: Cargando...");
    private final LineChart<Number, Number> rsiChart;

    public GraficoRSIView(String cryptoId) {
        super(10);
        setPadding(new Insets(10));

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Día");
        yAxis.setLabel("RSI");

        rsiChart = new LineChart<>(xAxis, yAxis);
        rsiChart.setTitle("RSI (14)");

        getChildren().addAll(rsiLabel, rsiChart);

        cargarDatosRSI(cryptoId);
    }

    private void cargarDatosRSI(String cryptoId) {
        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/cryptos/" + cryptoId + "/indicadores";
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.body());
                List<Double> rsi = mapper.convertValue(root.get("rsi"), new TypeReference<>() {});

                XYChart.Series<Number, Number> rsiSeries = new XYChart.Series<>();
                rsiSeries.setName("RSI");

                for (int i = 0; i < rsi.size(); i++) {
                    rsiSeries.getData().add(new XYChart.Data<>(i + 1, rsi.get(i)));
                }

                double rsiActual = rsi.isEmpty() ? -1 : rsi.get(rsi.size() - 1);

                Platform.runLater(() -> {
                    rsiChart.getData().add(rsiSeries);
                    if (rsiActual >= 0) {
                        rsiLabel.setText(String.format("RSI (14): %.2f", rsiActual));
                        rsiLabel.setStyle(rsiActual >= 70 ? "-fx-text-fill: green;"
                                : rsiActual <= 30 ? "-fx-text-fill: red;" : "-fx-text-fill: gray;");
                    } else {
                        rsiLabel.setText("RSI: no disponible");
                        rsiLabel.setStyle("-fx-text-fill: gray;");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> rsiLabel.setText("❌ Error al cargar RSI"));
                System.err.println("❌ Error al cargar RSI: " + e.getMessage());
            }
        }).start();
    }
} 

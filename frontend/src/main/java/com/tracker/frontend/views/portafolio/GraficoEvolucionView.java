package com.tracker.frontend.views.portafolio;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GraficoEvolucionView {

    private final String usuarioId;

    public GraficoEvolucionView(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void mostrar() {
        Stage stage = new Stage();
        stage.setTitle("Evolución del Portafolio");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Día");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Valor (€)");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Histórico del valor del portafolio");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Valor diario");

        VBox vbox = new VBox(10, lineChart);
        vbox.setPadding(new Insets(10));
        stage.setScene(new Scene(vbox, 600, 400));
        stage.show();

        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/portafolio/" + usuarioId + "/evolucion";
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                ObjectMapper mapper = new ObjectMapper();
                List<JsonNode> puntos = mapper.readValue(response.body(), new TypeReference<List<JsonNode>>() {});

                for (JsonNode punto : puntos) {
                    int dia = punto.get("dia").asInt();
                    double valor = punto.get("valor").asDouble();
                    series.getData().add(new XYChart.Data<>(dia, valor));
                }

                Platform.runLater(() -> lineChart.getData().add(series));

            } catch (Exception e) {
                System.err.println("❌ Error al cargar evolución del portafolio: " + e.getMessage());
            }
        }).start();
    }
} 

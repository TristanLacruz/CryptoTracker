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

public class GraficoRendimientoView {

    private final String usuarioId;

    public GraficoRendimientoView(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void mostrar() {
        Stage stage = new Stage();
        stage.setTitle("Ganancia/Pérdida acumulada");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Día");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Ganancia (€)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Rendimiento neto diario");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Ganancia acumulada");

        VBox vbox = new VBox(10, chart);
        vbox.setPadding(new Insets(10));
        stage.setScene(new Scene(vbox, 600, 400));
        stage.show();

        new Thread(() -> {
            try {
                String url = "http://localhost:8080/api/portafolio/" + usuarioId + "/rendimiento";
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                ObjectMapper mapper = new ObjectMapper();
                List<JsonNode> datos = mapper.readValue(response.body(), new TypeReference<List<JsonNode>>() {});

                for (JsonNode punto : datos) {
                    int dia = punto.get("dia").asInt();
                    double ganancia = punto.get("ganancia").asDouble();
                    series.getData().add(new XYChart.Data<>(dia, ganancia));
                }

                Platform.runLater(() -> chart.getData().add(series));
            } catch (Exception e) {
                System.err.println("Error al obtener rendimiento: " + e.getMessage());
            }
        }).start();
    }
}

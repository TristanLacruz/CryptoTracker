package com.tracker.frontend.views.graficos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.frontend.session.Session;
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

public class GraficoCombinadoView {

    private final String usuarioId;

    public GraficoCombinadoView(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void mostrar() {
        Stage stage = new Stage();
        stage.setTitle("Gráfico combinado del portafolio");

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Día");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Valor (€)");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Evolución y rendimiento del portafolio");

        XYChart.Series<Number, Number> evolucionSeries = new XYChart.Series<>();
        evolucionSeries.setName("Evolución diaria");

        XYChart.Series<Number, Number> rendimientoSeries = new XYChart.Series<>();
        rendimientoSeries.setName("Ganancia acumulada");

        VBox vbox = new VBox(10, chart);
        vbox.setPadding(new Insets(10));
        stage.setScene(new Scene(vbox, 700, 400));
        stage.show();

        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                ObjectMapper mapper = new ObjectMapper();

                // 📈 Evolución
                HttpRequest evRequest = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/portafolio/" + usuarioId + "/evolucion"))
                        .header("Authorization", "Bearer " + Session.idToken)
                        .GET()
                        .build();
                HttpResponse<String> evResponse = client.send(evRequest, HttpResponse.BodyHandlers.ofString());
                List<JsonNode> evData = mapper.readValue(evResponse.body(), new TypeReference<List<JsonNode>>() {});
                for (JsonNode punto : evData) {
                    int dia = punto.get("dia").asInt();
                    double valor = punto.get("valor").asDouble();
                    evolucionSeries.getData().add(new XYChart.Data<>(dia, valor));
                }

                // 💹 Rendimiento
                HttpRequest rdRequest = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/portafolio/" + usuarioId + "/rendimiento"))
                        .header("Authorization", "Bearer " + Session.idToken)
                        .GET()
                        .build();
                HttpResponse<String> rdResponse = client.send(rdRequest, HttpResponse.BodyHandlers.ofString());
                List<JsonNode> rdData = mapper.readValue(rdResponse.body(), new TypeReference<List<JsonNode>>() {});

                for (JsonNode punto : rdData) {
                    int dia = punto.get("dia").asInt();
                    double ganancia = punto.get("ganancia").asDouble();
                    rendimientoSeries.getData().add(new XYChart.Data<>(dia, ganancia));
                }

                Platform.runLater(() -> chart.getData().addAll(evolucionSeries, rendimientoSeries));

            } catch (Exception e) {
                System.err.println("❌ Error al cargar gráfico combinado: " + e.getMessage());
            }
        }).start();
    }
}

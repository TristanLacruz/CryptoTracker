package com.tracker.frontend.views;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CryptoChartsView extends VBox {

	
    private final String cryptoId;
    private final Label rsiLabel = new Label("RSI: Cargando...");
    private final LineChart<Number, Number> priceChart;
    private final LineChart<Number, Number> rsiChart;
    private final LineChart<Number, Number> macdChart;

    public CryptoChartsView(String cryptoId) {
        this.cryptoId = cryptoId;

        // Inicializar gráficos
        priceChart = crearChart("Histórico de Precio + SMA/EMA", "Día", "Precio (€)");
        rsiChart = crearChart("RSI (14)", "Día", "RSI");
        rsiChart.getYAxis().setAutoRanging(false);
        ((NumberAxis) rsiChart.getYAxis()).setLowerBound(0);
        ((NumberAxis) rsiChart.getYAxis()).setUpperBound(100);
        ((NumberAxis) rsiChart.getYAxis()).setTickUnit(10);

        macdChart = crearChart("MACD y Señal", "Día", "MACD");

        setSpacing(10);
        setPadding(new javafx.geometry.Insets(10));
        getChildren().addAll(rsiLabel, priceChart, rsiChart, macdChart);

        cargarDatos();
    }

    private LineChart<Number, Number> crearChart(String titulo, String xLabel, String yLabel) {
        NumberAxis x = new NumberAxis(); x.setLabel(xLabel);
        NumberAxis y = new NumberAxis(); y.setLabel(yLabel);
        LineChart<Number, Number> chart = new LineChart<>(x, y);
        chart.setTitle(titulo);
        return chart;
    }

    private void cargarDatos() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/api/cryptos/" + cryptoId + "/indicadores"))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.body());

                List<Double> precios = mapper.convertValue(root.get("precios"), new TypeReference<List<Double>>() {});
                List<Double> sma = mapper.convertValue(root.get("sma"), new TypeReference<List<Double>>() {});
                List<Double> ema = mapper.convertValue(root.get("ema"), new TypeReference<List<Double>>() {});
                List<Double> rsi = mapper.convertValue(root.get("rsi"), new TypeReference<List<Double>>() {});
                List<Double> macd = mapper.convertValue(root.get("macd"), new TypeReference<List<Double>>() {});
                List<Double> signal = mapper.convertValue(root.get("signal"), new TypeReference<List<Double>>() {});


                XYChart.Series<Number, Number> priceSeries = crearSerie("Precio", precios, 1);
                XYChart.Series<Number, Number> smaSeries = crearSerie("SMA", sma, 7);
                XYChart.Series<Number, Number> emaSeries = crearSerie("EMA", ema, 7);
                XYChart.Series<Number, Number> rsiSeries = crearSerie("RSI", rsi, 1);
                XYChart.Series<Number, Number> macdSeries = crearSerie("MACD", macd, 1);
                XYChart.Series<Number, Number> signalSeries = crearSerie("Señal", signal, 1 + (macd.size() - signal.size()));

                double rsiActual = rsi.isEmpty() ? -1 : rsi.get(rsi.size() - 1);

                Platform.runLater(() -> {
                    priceChart.getData().addAll(priceSeries, smaSeries, emaSeries);
                    rsiChart.getData().add(rsiSeries);
                    macdChart.getData().addAll(macdSeries, signalSeries);

                    if (rsiActual >= 0) {
                        rsiLabel.setText(String.format("RSI (14): %.2f", rsiActual));
                        rsiLabel.setStyle(rsiActual >= 70 ? "-fx-text-fill: green;" :
                                rsiActual <= 30 ? "-fx-text-fill: red;" : "-fx-text-fill: gray;");
                    } else {
                        rsiLabel.setText("RSI: no disponible");
                        rsiLabel.setStyle("-fx-text-fill: gray;");
                    }
                });

            } catch (Exception e) {
                System.err.println("❌ Error al cargar gráficos: " + e.getMessage());
            }
        }).start();
    }

    private XYChart.Series<Number, Number> crearSerie(String nombre, List<Double> datos, int desplazamiento) {
        XYChart.Series<Number, Number> serie = new XYChart.Series<>();
        serie.setName(nombre);
        for (int i = 0; i < datos.size(); i++) {
            serie.getData().add(new XYChart.Data<>(i + desplazamiento, datos.get(i)));
        }
        return serie;
    }
}

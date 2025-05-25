package com.tracker.frontend.views.graficos;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.frontend.session.Session;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GraficoMACDView extends VBox {

    private final NumberAxis yAxis;
    private final LineChart<Number, Number> chart;

    public GraficoMACDView(String cryptoId) {
        super(10);
        setPadding(new Insets(10));

        NumberAxis xAxis = new NumberAxis();
        yAxis = new NumberAxis();

        this.chart = new LineChart<>(xAxis, yAxis); 
        chart.setMinHeight(250);
        chart.setPrefHeight(300);

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
    			HttpRequest request = HttpRequest.newBuilder()
    				.uri(URI.create(url))
    				.header("Authorization", "Bearer " + Session.idToken)
    				.build();

    			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    			ObjectMapper mapper = new ObjectMapper();
    			JsonNode root = mapper.readTree(response.body());

    			JsonNode macdNode = root.get("macd");
    			JsonNode signalNode = root.get("signal");

    			if (macdNode == null || signalNode == null) {
    				System.err.println("Nodo 'macd' o 'signal' no encontrado.");
    				return;
    			}

    			List<Double> macd = mapper.convertValue(macdNode, new TypeReference<List<Double>>() {});
    			List<Double> signal = mapper.convertValue(signalNode, new TypeReference<List<Double>>() {});

    			XYChart.Series<Number, Number> macdSeries = new XYChart.Series<>();
    			macdSeries.setName("MACD");

    			XYChart.Series<Number, Number> signalSeries = new XYChart.Series<>();
    			signalSeries.setName("Señal");

    			int offset = macd.size() - signal.size();

    			for (int i = 0; i < macd.size(); i++) {
    				macdSeries.getData().add(new XYChart.Data<>(i + 1, macd.get(i)));
    			}
    			for (int i = 0; i < signal.size(); i++) {
    				signalSeries.getData().add(new XYChart.Data<>(i + 1 + offset, signal.get(i)));
    			}

    			List<Double> combinados = new java.util.ArrayList<>();
    			combinados.addAll(macd);
    			combinados.addAll(signal);

    			double min = combinados.stream().min(Double::compareTo).orElse(-1.0);
    			double max = combinados.stream().max(Double::compareTo).orElse(1.0);

    			Platform.runLater(() -> {
    				yAxis.setAutoRanging(false);
    				yAxis.setLowerBound(min * 0.95);
    				yAxis.setUpperBound(max * 1.05);
    				yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 10);

    				chart.getData().clear();
    				chart.getData().addAll(macdSeries, signalSeries);
    				chart.setTitle("MACD: " + String.format("%.2f", macd.get(macd.size() - 1)) + " | Señal: " + String.format("%.2f", signal.get(signal.size() - 1)));     

    				macdSeries.getNode().setStyle("-fx-stroke: #00FF00; -fx-stroke-width: 2.5px;"); 
    				signalSeries.getNode().setStyle("-fx-stroke: #448AFF; -fx-stroke-width: 2.5px;"); 
    			});

    		} catch (Exception e) {
    			System.err.println("Error al cargar MACD: " + e.getMessage());
    		}
    	}).start();
    }

}

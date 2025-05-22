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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class GraficoRSIView extends VBox {

	private final LineChart<Number, Number> rsiChart;
	private final NumberAxis yAxis;

	public GraficoRSIView(String cryptoId) {
		super(10);
		setPadding(new Insets(10));

		NumberAxis xAxis = new NumberAxis();
		yAxis = new NumberAxis();

		rsiChart = new LineChart<>(xAxis, yAxis);

		Label ejeXLabel = new Label("Días");
		ejeXLabel.setStyle("-fx-text-fill: #00FF00; -fx-font-family: Consolas; -fx-font-size: 10px;");
		HBox labelContainer = new HBox(ejeXLabel);
		labelContainer.setAlignment(Pos.CENTER_RIGHT);
		labelContainer.setPadding(new Insets(0, 10, 0, 0));

		getChildren().addAll(rsiChart, labelContainer);

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
				List<Double> rsi = mapper.convertValue(root.get("rsi"), new TypeReference<List<Double>>() {
				});

				XYChart.Series<Number, Number> rsiSeries = new XYChart.Series<>();
				rsiSeries.setName("RSI");

				for (int i = 0; i < rsi.size(); i++) {
					rsiSeries.getData().add(new XYChart.Data<>(i + 1, rsi.get(i)));
				}

				double rsiActual = rsi.isEmpty() ? -1 : rsi.get(rsi.size() - 1);
				double min = rsi.stream().min(Double::compareTo).orElse(0.0);
				double max = rsi.stream().max(Double::compareTo).orElse(100.0);

				Platform.runLater(() -> {
					yAxis.setAutoRanging(false);
					yAxis.setLowerBound(Math.max(0, min * 0.95));
					yAxis.setUpperBound(Math.min(100, max * 1.05));
					yAxis.setTickUnit((yAxis.getUpperBound() - yAxis.getLowerBound()) / 10);

					rsiChart.getData().clear();
					rsiChart.getData().add(rsiSeries);
					rsiSeries.getNode().setStyle("-fx-stroke: #448AFF; -fx-stroke-width: 2.5px;");

					if (rsiActual >= 0) {
						rsiChart.setTitle(String.format("RSI (14 días): %.2f", rsiActual));
					}
				});

			} catch (Exception e) {
				System.err.println("❌ Error al cargar RSI: " + e.getMessage());
			}
		}).start();
	}
}

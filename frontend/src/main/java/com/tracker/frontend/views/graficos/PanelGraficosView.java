package com.tracker.frontend.views.graficos;

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

public class PanelGraficosView extends VBox {

	public PanelGraficosView(String cryptoId) {
		setSpacing(10);
		setPadding(new Insets(10));

		GraficoRSIView graficoRSI = new GraficoRSIView(cryptoId);
		GraficoMACDView graficoMACD = new GraficoMACDView(cryptoId);

		getChildren().addAll(graficoRSI, graficoMACD);

	}
}

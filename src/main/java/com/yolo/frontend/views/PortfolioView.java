package com.yolo.frontend.views;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolo.frontend.dto.CriptoPosesionDTO;
import com.yolo.frontend.views.componentes.BotonesPortafolioView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class PortfolioView {

    private final String usuarioId;

    public PortfolioView(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public void mostrar() {
        Stage stage = new Stage();
        stage.setTitle("Mi Portafolio");

        TableView<CriptoPosesionDTO> tableView = new TableView<>();
        ObservableList<CriptoPosesionDTO> data = FXCollections.observableArrayList();

        Label totalLabel = new Label("Total del portafolio: €0.00");

        // Botones
        BotonesPortafolioView botones = new BotonesPortafolioView(
        	    v -> mostrarGraficaEvolucion(),
        	    v -> mostrarGraficaRendimiento(),
        	    v -> mostrarGraficaCombinada()
        	);

        // Tabla
        TableColumn<CriptoPosesionDTO, String> simboloCol = new TableColumn<>("Criptomoneda");
        simboloCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("simbolo"));

        TableColumn<CriptoPosesionDTO, Double> cantidadCol = new TableColumn<>("Cantidad");
        cantidadCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("cantidad"));
        cantidadCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double cantidad, boolean empty) {
                super.updateItem(cantidad, empty);
                setText(empty || cantidad == null ? null : String.format("%,.6f", cantidad));
            }
        });

        TableColumn<CriptoPosesionDTO, Double> valorCol = new TableColumn<>("Valor Total (€)");
        valorCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("valorTotal"));
        valorCol.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double valor, boolean empty) {
                super.updateItem(valor, empty);
                setText(empty || valor == null ? null : String.format("€%,.2f", valor));
            }
        });

        tableView.getColumns().addAll(simboloCol, cantidadCol, valorCol);
        tableView.setItems(data);

        VBox vbox = new VBox(10,
                new Label("Portafolio del usuario: " + usuarioId),
                tableView,
                totalLabel,
                botones
        );
        vbox.setPadding(new Insets(10));

        stage.setScene(new Scene(vbox, 600, 450));
        stage.show();

        // Cargar datos
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();

                String url = "http://localhost:8080/api/portafolio/" + usuarioId + "/resumen";
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                ObjectMapper mapper = new ObjectMapper();
                List<CriptoPosesionDTO> lista = mapper.readValue(response.body(), new TypeReference<>() {});

                double total = lista.stream()
                        .mapToDouble(CriptoPosesionDTO::getValorTotal)
                        .sum();

                String urlInversion = "http://localhost:8080/api/transacciones/invertido/" + usuarioId;
                HttpRequest invRequest = HttpRequest.newBuilder().uri(URI.create(urlInversion)).build();
                HttpResponse<String> invResponse = client.send(invRequest, HttpResponse.BodyHandlers.ofString());

                double invertido = Double.parseDouble(invResponse.body());
                double diferencia = total - invertido;
                double rendimiento = invertido == 0 ? 0 : (diferencia / invertido) * 100;

                String resumen = String.format("💼 Total del portafolio: €%,.2f\n📊 Rendimiento: %+.2f € (%.2f%%)",
                        total, diferencia, rendimiento);

                Platform.runLater(() -> {
                    data.addAll(lista);
                    totalLabel.setText(resumen);
                });

            } catch (Exception e) {
                System.err.println("❌ Error al obtener portafolio: " + e.getMessage());
            }
        }).start();
    }

    private void mostrarGraficaEvolucion() {
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

	    // Obtener los datos desde el backend
	    new Thread(() -> {
	        try {
	            String url = "http://localhost:8080/api/portafolio/" + usuarioId + "/evolucion";
	            HttpClient client = HttpClient.newHttpClient();
	            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
	            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	            ObjectMapper mapper = new ObjectMapper();
	            List<JsonNode> puntos = mapper.readValue(response.body(), new TypeReference<>() {});

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


    private void mostrarGraficaRendimiento() {
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
	            List<JsonNode> datos = mapper.readValue(response.body(), new TypeReference<>() {});

	            for (JsonNode punto : datos) {
	                int dia = punto.get("dia").asInt();
	                double ganancia = punto.get("ganancia").asDouble();
	                series.getData().add(new XYChart.Data<>(dia, ganancia));
	            }

	            Platform.runLater(() -> chart.getData().add(series));
	        } catch (Exception e) {
	            System.err.println("❌ Error al obtener rendimiento: " + e.getMessage());
	        }
	    }).start();
	}

    private void mostrarGraficaCombinada() {
        // Llama a tu método mostrarGraficaCombinada(usuarioId)
    }
}

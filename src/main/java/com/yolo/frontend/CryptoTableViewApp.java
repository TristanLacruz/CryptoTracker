package com.yolo.frontend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.collections.transformation.FilteredList;
import com.yolo.frontend.dto.CryptoMarketDTO;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CryptoTableViewApp extends Application {

    private final ObservableList<CryptoMarketDTO> cryptoList = FXCollections.observableArrayList();
    private final String API_URL = "http://localhost:8080/cryptos/market";

    @Override
    public void start(Stage primaryStage) {
        TableView<CryptoMarketDTO> tableView = new TableView<>();

        TableColumn<CryptoMarketDTO, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<CryptoMarketDTO, String> symbolCol = new TableColumn<>("Símbolo");
        symbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));

        TableColumn<CryptoMarketDTO, Double> priceCol = new TableColumn<>("Precio (EUR)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));

        TableColumn<CryptoMarketDTO, Double> changeCol = new TableColumn<>("% Cambio 24h");
        changeCol.setCellValueFactory(new PropertyValueFactory<>("priceChangePercentage24h"));

        TableColumn<CryptoMarketDTO, Double> volumeCol = new TableColumn<>("Volumen");
        volumeCol.setCellValueFactory(new PropertyValueFactory<>("totalVolume"));

        tableView.getColumns().addAll(nameCol, symbolCol, priceCol, changeCol, volumeCol);

        // Buscador
        TextField buscador = new TextField();
        buscador.setPromptText("Buscar por nombre o símbolo...");

        FilteredList<CryptoMarketDTO> filteredData = new FilteredList<>(cryptoList, p -> true);
        buscador.textProperty().addListener((obs, oldVal, newVal) -> {
            String lower = newVal.toLowerCase();
            filteredData.setPredicate(crypto ->
                crypto.getName().toLowerCase().contains(lower) ||
                crypto.getSymbol().toLowerCase().contains(lower)
            );
        });

        tableView.setItems(filteredData);

        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                CryptoMarketDTO selected = tableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showCryptoDetails(selected);
                }
            }
        });

        // Diseño principal
        VBox layout = new VBox(10, buscador, tableView);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Listado de Criptomonedas");
        primaryStage.show();

        fetchCryptoData(); // Llamada al backend
    }


    private void fetchCryptoData() {
    	System.out.println("Intentando conectarse a: " + API_URL);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_URL)).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::parseJson)
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }

    private void parseJson(String responseBody) {
        System.out.println("✅ Entrando a parseJson()");
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<CryptoMarketDTO> list = mapper.readValue(responseBody, new TypeReference<>() {});
            System.out.println("✅ Criptos parseadas: " + list.size());

            javafx.application.Platform.runLater(() -> {
                System.out.println("✅ Actualizando tabla JavaFX");
                cryptoList.clear();
                cryptoList.addAll(list);
            });

        } catch (IOException e) {
            System.out.println("❌ Error al parsear JSON:");
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        launch();
    }
    
    private void showCryptoDetails(CryptoMarketDTO crypto) {
        System.out.println("ID de la cripto seleccionada: " + crypto.getId());
        Stage detailStage = new Stage();
        detailStage.setTitle("Detalles de " + crypto.getName());

        Label nombre = new Label("Nombre: " + crypto.getName());
        Label simbolo = new Label("Símbolo: " + crypto.getSymbol());
        Label precio = new Label("Precio actual: €" + crypto.getCurrentPrice());

        // Gráfico ficticio
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Día");
        yAxis.setLabel("Precio (EUR)");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Histórico de 7 días");

        XYChart.Series<Number, Number> serie = new XYChart.Series<>();
        serie.setName("Precio");

        List<Double> precios = getHistoricalPrices(crypto.getId());

        for (int i = 0; i < precios.size(); i++) {
            serie.getData().add(new XYChart.Data<>(i + 1, precios.get(i)));
        }

        lineChart.getData().add(serie);

        VBox vbox = new VBox(10, nombre, simbolo, precio, lineChart);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 500, 400);
        detailStage.setScene(scene);
        detailStage.show();
    }
    
    private List<Double> getHistoricalPrices(String cryptoId) {
        String url = "https://api.coingecko.com/api/v3/coins/" + cryptoId + "/market_chart?vs_currency=eur&days=7";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            JsonNode prices = root.get("prices");

            List<Double> priceList = new ArrayList<>();
            for (JsonNode point : prices) {
                priceList.add(point.get(1).asDouble()); // segundo valor = precio
            }
            return priceList;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


}

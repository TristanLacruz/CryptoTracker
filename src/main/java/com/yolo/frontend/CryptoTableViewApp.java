package com.yolo.frontend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.transformation.FilteredList;
import javafx.stage.Stage;
import com.yolo.frontend.dto.CryptoMarketDTO;
import com.yolo.frontend.services.CryptoService;

import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CryptoTableViewApp extends Application {

	private final ObservableList<CryptoMarketDTO> cryptoList = FXCollections.observableArrayList();
	String url = "http://localhost:8080/api/cryptos/market";

	@Override
	public void start(Stage primaryStage) {
		TableView<CryptoMarketDTO> tableView = new TableView<>();

		// Columnas de la tabla
		TableColumn<CryptoMarketDTO, String> nameCol = new TableColumn<>("Nombre");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

		TableColumn<CryptoMarketDTO, String> symbolCol = new TableColumn<>("Símbolo");
		symbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));

		// Columna de imagen
		TableColumn<CryptoMarketDTO, String> imageCol = new TableColumn<>("Icono");
		imageCol.setCellValueFactory(new PropertyValueFactory<>("image"));
		imageCol.setCellFactory(tc -> new TableCell<>() {
			private final ImageView imageView = new ImageView();
			{
				imageView.setFitHeight(30);
				imageView.setFitWidth(30);
			}

			@Override
			protected void updateItem(String imageUrl, boolean empty) {
				super.updateItem(imageUrl, empty);
				if (empty || imageUrl == null || imageUrl.isEmpty()) {
					setGraphic(null);
				} else {
					try {
						Image image = new Image(imageUrl, true); // true para carga en segundo plano
						imageView.setImage(image);
						setGraphic(imageView);
					} catch (Exception e) {
						setGraphic(null);
						System.err.println("Error cargando imagen: " + imageUrl);
					}
				}
			}
		});

		// Columna de precio con formato
		TableColumn<CryptoMarketDTO, Double> priceCol = new TableColumn<>("Precio (EUR)");
		priceCol.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
		priceCol.setCellFactory(tc -> new TableCell<>() {
			@Override
			protected void updateItem(Double price, boolean empty) {
				super.updateItem(price, empty);
				setText(empty || price == null ? null : String.format("€%,.2f", price));
			}
		});

		// Columna de % cambio con colores
		TableColumn<CryptoMarketDTO, Double> changeCol = new TableColumn<>("% Cambio 24h");
		changeCol.setCellValueFactory(new PropertyValueFactory<>("priceChangePercentage24h"));
		changeCol.setCellFactory(tc -> new TableCell<>() {
			@Override
			protected void updateItem(Double change, boolean empty) {
				super.updateItem(change, empty);
				if (empty || change == null) {
					setText(null);
					setStyle("");
				} else {
					setText(String.format("%.2f%%", change));
					setStyle(change < 0 ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
				}
			}
		});

		// Columna de volumen con formato
		TableColumn<CryptoMarketDTO, Double> volumeCol = new TableColumn<>("Volumen 24h");
		volumeCol.setCellValueFactory(new PropertyValueFactory<>("totalVolume"));
		volumeCol.setCellFactory(tc -> new TableCell<>() {
			@Override
			protected void updateItem(Double volume, boolean empty) {
				super.updateItem(volume, empty);
				setText(empty || volume == null ? null : String.format("€%,.0f", volume));
			}
		});

		// Nueva columna de capitalización de mercado
		TableColumn<CryptoMarketDTO, Double> marketCapCol = new TableColumn<>("Capitalización");
		marketCapCol.setCellValueFactory(new PropertyValueFactory<>("marketCap"));
		marketCapCol.setCellFactory(tc -> new TableCell<>() {
			@Override
			protected void updateItem(Double marketCap, boolean empty) {
				super.updateItem(marketCap, empty);
				setText(empty || marketCap == null ? null : String.format("€%,.0f", marketCap));
			}
		});

		tableView.getColumns().addAll(imageCol, nameCol, symbolCol, priceCol, changeCol, volumeCol, marketCapCol);

		// Buscador
		TextField buscador = new TextField();
		buscador.setPromptText("Buscar por nombre o símbolo...");

		FilteredList<CryptoMarketDTO> filteredData = new FilteredList<>(cryptoList, p -> true);
		buscador.textProperty().addListener((obs, oldVal, newVal) -> {
			String lower = newVal.toLowerCase();
			filteredData.setPredicate(crypto -> crypto.getName().toLowerCase().contains(lower)
					|| crypto.getSymbol().toLowerCase().contains(lower));
		});

		tableView.setItems(filteredData);

		// Evento de doble clic
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

		Scene scene = new Scene(layout, 1000, 600); // Aumenté el tamaño para acomodar más columnas
		primaryStage.setScene(scene);
		primaryStage.setTitle("Listado de Criptomonedas");
		primaryStage.show();

		fetchCryptoData();
	}

	private void fetchCryptoData() {
	    System.out.println("Intentando conectarse a CryptoService...");

	    CryptoService.getMarketData().thenAccept(list -> {
	        javafx.application.Platform.runLater(() -> {
	            System.out.println("✅ Datos recibidos: " + list.size() + " criptos.");
	            cryptoList.clear();
	            cryptoList.addAll(list);
	        });
	    }).exceptionally(e -> {
	        System.err.println("❌ Error al obtener criptos: " + e.getMessage());
	        e.printStackTrace();
	        return null;
	    });
	}


	private void parseJson(String responseBody) {
		System.out.println("✅ Entrando a parseJson()");
		System.out.println("Respuesta recibida:\n" + responseBody); // Depuración

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			List<CryptoMarketDTO> list = mapper.readValue(responseBody, new TypeReference<List<CryptoMarketDTO>>() {
			});
			System.out.println("✅ Criptos parseadas: " + list.size());

			// Verificación de datos
			list.forEach(crypto -> {
				System.out.printf("%s (%s) - Precio: %.2f, Cambio: %.2f%%, Volumen: %.0f, MarketCap: %.0f%n",
						crypto.getName(), crypto.getSymbol(), crypto.getCurrentPrice(),
						crypto.getPriceChangePercentage24h(), crypto.getTotalVolume(), crypto.getMarketCap());
			});

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

	private void showCryptoDetails(CryptoMarketDTO crypto) {
	    Stage detailStage = new Stage();
	    detailStage.setTitle("Detalles de " + crypto.getName());

	    // Labels
	    Label nombre = new Label("Nombre: " + crypto.getName());
	    Label simbolo = new Label("Símbolo: " + crypto.getSymbol());
	    Label precio = new Label(String.format("Precio actual: €%,.2f", crypto.getCurrentPrice()));
	    Label cambio = new Label(String.format("Cambio 24h: %.2f%%", crypto.getPriceChangePercentage24h()));
	    Label volumen = new Label(String.format("Volumen 24h: €%,.0f", crypto.getTotalVolume()));
	    Label marketCap = new Label(String.format("Capitalización: €%,.0f", crypto.getMarketCap()));
	    Label rsiLabel = new Label("RSI: Cargando...");

	    // Gráfico de precios
	    final NumberAxis xAxisPrice = new NumberAxis();
	    final NumberAxis yAxisPrice = new NumberAxis();
	    xAxisPrice.setLabel("Día");
	    yAxisPrice.setLabel("Precio (EUR)");
	    final LineChart<Number, Number> priceChart = new LineChart<>(xAxisPrice, yAxisPrice);
	    priceChart.setTitle("Histórico de Precio (7 días)");

	    // Gráfico RSI
	    final NumberAxis xAxisRSI = new NumberAxis();
	    final NumberAxis yAxisRSI = new NumberAxis(0, 100, 10);
	    xAxisRSI.setLabel("Día");
	    yAxisRSI.setLabel("RSI (14)");
	    final LineChart<Number, Number> rsiChart = new LineChart<>(xAxisRSI, yAxisRSI);
	    rsiChart.setTitle("Índice RSI (14) - 7 días");

	    // Layout
	    VBox vbox = new VBox(10, nombre, simbolo, precio, cambio, volumen, marketCap, rsiLabel, priceChart, rsiChart);
	    vbox.setPadding(new Insets(10));

	    // Cargar RSI actual
	    new Thread(() -> {
	        try {
	            String url = "http://localhost:8080/api/cryptos/" + crypto.getId() + "/rsi";
	            HttpClient client = HttpClient.newHttpClient();
	            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
	            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	            double rsi = Double.parseDouble(response.body());

	            javafx.application.Platform.runLater(() -> {
	                rsiLabel.setText(String.format("RSI (14): %.2f", rsi));
	                if (rsi >= 70) {
	                    rsiLabel.setStyle("-fx-text-fill: green;");
	                } else if (rsi <= 30) {
	                    rsiLabel.setStyle("-fx-text-fill: red;");
	                } else {
	                    rsiLabel.setStyle("-fx-text-fill: gray;");
	                }
	            });
	        } catch (Exception e) {
	            javafx.application.Platform.runLater(() -> {
	                rsiLabel.setText("RSI: no disponible");
	                rsiLabel.setStyle("-fx-text-fill: gray;");
	            });
	        }
	    }).start();

	    // Cargar precios históricos
	    new Thread(() -> {
	        List<Double> precios = getHistoricalPrices(crypto.getId());
	        XYChart.Series<Number, Number> priceSeries = new XYChart.Series<>();
	        priceSeries.setName("Precio");

	        for (int i = 0; i < precios.size(); i++) {
	            priceSeries.getData().add(new XYChart.Data<>(i + 1, precios.get(i)));
	        }

	        // Rango dinámico del eje Y
	        double minPrice = precios.stream().mapToDouble(Double::doubleValue).min().orElse(0);
	        double maxPrice = precios.stream().mapToDouble(Double::doubleValue).max().orElse(1);
	        double margen = (maxPrice - minPrice) * 0.1;

	        javafx.application.Platform.runLater(() -> {
	            yAxisPrice.setAutoRanging(false);
	            yAxisPrice.setLowerBound(minPrice - margen);
	            yAxisPrice.setUpperBound(maxPrice + margen);
	            yAxisPrice.setTickUnit((maxPrice - minPrice) / 10);
	            priceChart.getData().add(priceSeries);
	        });
	    }).start();

	 // Cargar RSI histórico
	    new Thread(() -> {
	        try {
	            String rsiUrl = "http://localhost:8080/api/cryptos/" + crypto.getId() + "/rsi/history";
	            HttpClient client = HttpClient.newHttpClient();
	            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(rsiUrl)).build();
	            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	            ObjectMapper mapper = new ObjectMapper();
	            List<Double> rsiValues = mapper.readValue(response.body(), new TypeReference<List<Double>>() {});

	            System.out.println("📊 RSI histórico recibido: " + rsiValues);

	            if (rsiValues.isEmpty()) {
	                javafx.application.Platform.runLater(() -> {
	                    rsiLabel.setText("RSI: no disponible (insuficientes datos)");
	                    rsiLabel.setStyle("-fx-text-fill: gray;");
	                });
	                return;
	            }

	            XYChart.Series<Number, Number> rsiSeries = new XYChart.Series<>();
	            rsiSeries.setName("RSI (14)");

	            for (int i = 0; i < rsiValues.size(); i++) {
	                Double rsi = rsiValues.get(i);
	                if (rsi != null) {
	                    rsiSeries.getData().add(new XYChart.Data<>(i + 1, rsi));
	                }
	            }

	            // Calcular rango dinámico del eje Y para RSI
	            double minRSI = rsiValues.stream().mapToDouble(Double::doubleValue).min().orElse(0);
	            double maxRSI = rsiValues.stream().mapToDouble(Double::doubleValue).max().orElse(100);
	            double margenRSI = (maxRSI - minRSI) * 0.1;

	            javafx.application.Platform.runLater(() -> {
	                yAxisRSI.setAutoRanging(false);
	                yAxisRSI.setLowerBound(minRSI - margenRSI);
	                yAxisRSI.setUpperBound(maxRSI + margenRSI);
	                yAxisRSI.setTickUnit((maxRSI - minRSI) / 10);
	                rsiChart.getData().add(rsiSeries);
	            });
	        } catch (Exception e) {
	            System.err.println("Error al obtener RSI histórico: " + e.getMessage());
	        }
	    }).start();


	    Scene scene = new Scene(vbox, 600, 500);
	    detailStage.setScene(scene);
	    detailStage.show();
	}

	private List<Double> getHistoricalPrices(String cryptoId) {
	    String url = "http://localhost:8080/api/cryptos/" + cryptoId + "/historical";

	    if (cryptoId == null || cryptoId.isBlank()) {
	        throw new IllegalArgumentException("ID de criptomoneda no puede ser nulo o vacío.");
	    }

	    System.out.println("🧪 Solicitando históricos desde frontend para ID: " + cryptoId);

	    try {
	        HttpClient client = HttpClient.newHttpClient();
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(url))
	                .build();

	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	        // 🔥 Aquí mapeamos el objeto completo, no solo un array
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode root = mapper.readTree(response.body());
	        JsonNode prices = root.get("prices");

	        System.out.println("📦 JSON de históricos:\n" + response.body());

	        if (prices == null || !prices.isArray()) {
	            System.err.println("No se encontraron datos históricos para: " + cryptoId);
	            return List.of(); // vacío
	        }

	        List<Double> priceList = new ArrayList<>();
	        for (JsonNode point : prices) {
	            priceList.add(point.get(1).asDouble()); // solo el precio
	        }

	        return priceList;

	    } catch (Exception e) {
	        System.err.println("Error al obtener precios históricos para " + cryptoId + ": " + e.getMessage());
	        return List.of();
	    }
	}



	public static void main(String[] args) {
		launch();
	}
}
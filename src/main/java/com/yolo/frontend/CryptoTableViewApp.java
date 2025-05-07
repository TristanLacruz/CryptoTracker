package com.yolo.frontend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.transformation.FilteredList;
import javafx.stage.Stage;

import com.yolo.frontend.dto.CriptoPosesionDTO;
import com.yolo.frontend.dto.CryptoMarketDTO;
import com.yolo.frontend.services.CryptoService;
import com.yolo.frontend.views.CryptoDetailView;
import com.yolo.frontend.views.LoginFormView;
import com.yolo.frontend.views.PortfolioView;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CryptoTableViewApp extends Application {

	private final ObservableList<CryptoMarketDTO> cryptoList = FXCollections.observableArrayList();
	private ScheduledExecutorService scheduler;

	String url = "http://localhost:8080/api/cryptos/market";

	@Override
	public void start(Stage primaryStage) {
	    new LoginFormView().mostrarLogin(primaryStage);
	}

	
	public void mostrarAppPrincipal(Stage primaryStage) {
		TableView<CryptoMarketDTO> tableView = new TableView<>();
		String usuarioId = AuthContext.getInstance().getUsuarioId();

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

		tableView.setOnMouseClicked(event -> {
		    if (event.getClickCount() == 2) {
		        CryptoMarketDTO selected = tableView.getSelectionModel().getSelectedItem();
		        if (selected != null) {
		            String cryptoId = selected.getId();
		            String nombreCrypto = selected.getName();
		            double precioActual = selected.getCurrentPrice();

		            new CryptoDetailView(cryptoId, nombreCrypto, precioActual).mostrar(); // ✅ Correcto
		        }
		    }
		});


		// Diseño principal
		VBox layout = new VBox(10, buscador, tableView);
		layout.setPadding(new Insets(10));

		// Botón para mostrar el portafolio
		Button verPortafolioBtn = new Button("Ver mi Portafolio");
		verPortafolioBtn.setOnAction(e -> new PortfolioView(usuarioId).mostrar());

		// Añadir botón al layout
		layout.getChildren().add(0, verPortafolioBtn); // lo añade arriba del buscador

		Scene scene = new Scene(layout, 1000, 600); // Aumenté el tamaño para acomodar más columnas
		primaryStage.setScene(scene);
		primaryStage.setTitle("Listado de Criptomonedas");
		primaryStage.show();

		fetchCryptoData();

		/*
		 * SCHEDULER COMENTADO PARA EVITAR PETICIONES CONSTANTES
		 */
//		scheduler = Executors.newSingleThreadScheduledExecutor();
//		scheduler.scheduleAtFixedRate(() -> {
//		    javafx.application.Platform.runLater(this::fetchCryptoData);
//		}, 60, 60, TimeUnit.SECONDS); // espera 60s y luego repite cada 60s
		// NO ELIMNAR EL SCHEDULER, SOLO COMENTAR
	}

	@Override
	public void stop() throws Exception {
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdownNow();
		}
		super.stop();
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

	

	private List<Double> getHistoricalPrices(String cryptoId) {
		String url = "http://localhost:8080/api/cryptos/" + cryptoId + "/historical";

		if (cryptoId == null || cryptoId.isBlank()) {
			throw new IllegalArgumentException("ID de criptomoneda no puede ser nulo o vacío.");
		}

		System.out.println("🧪 Solicitando históricos desde frontend para ID: " + cryptoId);

		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

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
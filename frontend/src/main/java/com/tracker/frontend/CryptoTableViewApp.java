package com.tracker.frontend;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.scene.layout.VBox;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.stage.Stage;

import com.tracker.common.dto.CriptoPosesionDTO;
import com.tracker.common.dto.CryptoMarketDTO;
import com.tracker.frontend.services.CryptoService;
import com.tracker.frontend.util.FavoritosStorage;
import com.tracker.frontend.util.InactivityTimer;
import com.tracker.frontend.views.AnimatedBackgroundView;
import com.tracker.frontend.views.CryptoDetailView;
import com.tracker.frontend.views.LoginFormView;
import com.tracker.frontend.views.PortfolioView;
import com.tracker.frontend.session.Session;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Clase principal de la aplicación JavaFX que muestra una tabla de criptomonedas.
 * Permite al usuario ver información sobre criptomonedas y realizar acciones como
 * agregar a favoritos y ver detalles.
 */
public class CryptoTableViewApp extends Application {

	private final TableView<CryptoMarketDTO> tableView = new TableView<>();
	private final ObservableList<CryptoMarketDTO> cryptoList = FXCollections.observableArrayList();
	private ScheduledExecutorService scheduler;
	private final Set<String> favoritosGuardados = FavoritosStorage.cargarFavoritos();

	String url = "http://localhost:8080/api/cryptos/market";

	/**
	 * Método que se ejecuta al iniciar la aplicación.
	 * Crea la ventana principal y muestra la tabla de criptomonedas.
	 *
	 * @param primaryStage La ventana principal de la aplicación.
	 */
	@Override
	public void start(Stage primaryStage) {
		System.out.println("Se está cargando CryptoTableViewApp");
		new LoginFormView().mostrar(primaryStage);
	}

	/**
	 * Muestra la aplicación principal con la tabla de criptomonedas.
	 *
	 * @param primaryStage La ventana principal de la aplicación.
	 */
	public void mostrarAppPrincipal(Stage primaryStage) {
		String usuarioId = AuthContext.getInstance().getUsuarioId();

		TableColumn<CryptoMarketDTO, String> nameCol = new TableColumn<>("Nombre");
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameCol.setMaxWidth(1f * Integer.MAX_VALUE * 20); 
		
		TableColumn<CryptoMarketDTO, String> symbolCol = new TableColumn<>("Símbolo");
		symbolCol.setCellValueFactory(new PropertyValueFactory<>("symbol"));
		symbolCol.setMaxWidth(1f * Integer.MAX_VALUE * 10); 

		TableColumn<CryptoMarketDTO, Boolean> favCol = new TableColumn<>("⭐");
		favCol.setSortable(true); 
		favCol.setPrefWidth(40); 
		favCol.setMinWidth(40);
		favCol.setMaxWidth(40);

		favCol.setCellValueFactory(cellData -> {
		    CryptoMarketDTO crypto = cellData.getValue();
		    return new javafx.beans.property.SimpleBooleanProperty(crypto.isFavorito());
		});

		favCol.setCellFactory(new Callback<TableColumn<CryptoMarketDTO, Boolean>, TableCell<CryptoMarketDTO, Boolean>>() {
		    @Override
		    public TableCell<CryptoMarketDTO, Boolean> call(TableColumn<CryptoMarketDTO, Boolean> param) {
		        return new TableCell<CryptoMarketDTO, Boolean>() {
		            private final ImageView starView = new ImageView();
		            private final Image starEmpty = new Image(getClass().getResourceAsStream("/images/star_empty.png"), 20, 20, true, true);
		            private final Image starFilled = new Image(getClass().getResourceAsStream("/images/star_filled.png"), 20, 20, true, true);

		            {
		                setGraphic(starView);
		                setOnMouseClicked(e -> {
		                    CryptoMarketDTO crypto = getTableView().getItems().get(getIndex());
		                    ScaleTransition st = new ScaleTransition(Duration.millis(150), starView);
		                    st.setFromX(1.0);
		                    st.setFromY(1.0);
		                    st.setToX(1.3);
		                    st.setToY(1.3);
		                    st.setAutoReverse(true);
		                    st.setCycleCount(2);
		                    st.play();

		                    crypto.setFavorito(!crypto.isFavorito());
		                    starView.setImage(crypto.isFavorito() ? starFilled : starEmpty);
		                    tableView.sort(); 
		                    if (crypto.isFavorito()) {
		                        favoritosGuardados.add(crypto.getId());
		                    } else {
		                        favoritosGuardados.remove(crypto.getId());
		                    }
		                    FavoritosStorage.guardarFavoritos(favoritosGuardados);
		                });
		            }

		            @Override
		            protected void updateItem(Boolean isFav, boolean empty) {
		                super.updateItem(isFav, empty);
		                starView.setOnMouseEntered(e -> starView.setOpacity(0.7));
		                starView.setOnMouseExited(e -> starView.setOpacity(1.0));

		                if (empty || isFav == null) {
		                    setGraphic(null);
		                } else {
		                    starView.setImage(isFav ? starFilled : starEmpty);
		                    setGraphic(starView);
		                    setAlignment(Pos.CENTER); 
		                }
		            }
		        };
		    }
		});

		TableColumn<CryptoMarketDTO, String> imageCol = new TableColumn<>("Icono");
		imageCol.setCellValueFactory(new PropertyValueFactory<>("image"));
		imageCol.setCellFactory(tc -> new TableCell<CryptoMarketDTO, String>() {
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
						Image image = new Image(imageUrl, true);
						imageView.setImage(image);
						setGraphic(imageView);
					} catch (Exception e) {
						setGraphic(null);
						System.err.println("Error cargando imagen: " + imageUrl);
					}
				}
			}
		});

		TableColumn<CryptoMarketDTO, Double> priceCol = new TableColumn<>("Precio (EUR)");
		priceCol.setSortable(true);
		priceCol.setCellValueFactory(new PropertyValueFactory<>("currentPrice"));
		priceCol.setMaxWidth(1f * Integer.MAX_VALUE * 15);
		priceCol.setCellFactory(tc -> new TableCell<CryptoMarketDTO, Double>() {
			@Override
			protected void updateItem(Double price, boolean empty) {
				super.updateItem(price, empty);
				setText(empty || price == null ? null : String.format("€%,.2f", price));
			}
		});

		TableColumn<CryptoMarketDTO, Double> changeCol = new TableColumn<>("% Cambio 24h");
		changeCol.setCellValueFactory(new PropertyValueFactory<>("priceChangePercentage24h"));
		changeCol.setCellFactory(tc -> new TableCell<CryptoMarketDTO, Double>() {
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

		TableColumn<CryptoMarketDTO, Double> volumeCol = new TableColumn<>("Volumen 24h");
		volumeCol.setSortable(true); 
		volumeCol.setCellValueFactory(new PropertyValueFactory<>("totalVolume"));
		volumeCol.setCellFactory(tc -> new TableCell<CryptoMarketDTO, Double>() {
			@Override
			protected void updateItem(Double volume, boolean empty) {
				super.updateItem(volume, empty);
				setText(empty || volume == null ? null : String.format("€%,.0f", volume));
			}
		});

		TableColumn<CryptoMarketDTO, Double> marketCapCol = new TableColumn<>("Capitalización");
		marketCapCol.setSortable(true);
		marketCapCol.setCellValueFactory(new PropertyValueFactory<>("marketCap"));
		marketCapCol.setCellFactory(tc -> new TableCell<CryptoMarketDTO, Double>() {
			@Override
			protected void updateItem(Double marketCap, boolean empty) {
				super.updateItem(marketCap, empty);
				setText(empty || marketCap == null ? null : String.format("€%,.0f", marketCap));
			}
		});

		tableView.getColumns().addAll(imageCol, favCol, nameCol, symbolCol, priceCol, changeCol, volumeCol, marketCapCol);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setMaxWidth(Double.MAX_VALUE);

		TextField buscador = new TextField();
		buscador.setPromptText("Buscar por nombre o símbolo...");
		buscador.setPrefWidth(Double.MAX_VALUE);

		FilteredList<CryptoMarketDTO> filteredData = new FilteredList<>(cryptoList, p -> true);
		buscador.textProperty().addListener((obs, oldVal, newVal) -> {
		    String lower = newVal.toLowerCase();
		    filteredData.setPredicate(crypto -> crypto.getName().toLowerCase().contains(lower)
		            || crypto.getSymbol().toLowerCase().contains(lower));
		});

		SortedList<CryptoMarketDTO> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());
		tableView.setItems(sortedData);
		favCol.setSortType(TableColumn.SortType.DESCENDING);
		tableView.getSortOrder().setAll(favCol);

		tableView.getSortOrder().addListener((javafx.collections.ListChangeListener<? super TableColumn<?, ?>>) change -> {
		    System.out.println("Orden solicitado:");
		    for (TableColumn<?, ?> col : tableView.getSortOrder()) {
		        System.out.println(" - Columna: " + col.getText() + " | Orden: " + col.getSortType());
		    }
		});

		tableView.setFixedCellSize(40); // Altura fija por fila
		tableView.setPrefHeight(Region.USE_COMPUTED_SIZE);
		tableView.setMinHeight(Region.USE_COMPUTED_SIZE);
		tableView.setMaxHeight(Region.USE_COMPUTED_SIZE);

		tableView.setOnMouseClicked(event -> {
			if (event.getClickCount() == 2) {
				CryptoMarketDTO selected = tableView.getSelectionModel().getSelectedItem();
				if (selected != null) {
					new CryptoDetailView(selected.getId(), selected.getName(), selected.getCurrentPrice()).mostrar();
					primaryStage.close();
				}
			}
		});

		Button verPortafolioBtn = new Button("Ver mi Portafolio");
		verPortafolioBtn.setOnAction(e -> new PortfolioView(usuarioId).mostrar());

		Button btnCerrarSesion = new Button("Cerrar sesión");
		btnCerrarSesion.setOnAction(e -> {
			Session.idToken = null;
			primaryStage.close();
			new LoginFormView().mostrar(new Stage());
		});

		HBox topBar = new HBox(10, verPortafolioBtn, btnCerrarSesion);
		topBar.setAlignment(Pos.CENTER_LEFT);
		topBar.setPadding(new Insets(10));

		VBox topContent = new VBox(10, topBar, buscador);
		topContent.setPadding(new Insets(10));
		topContent.setFillWidth(true);

		BorderPane borderPane = new BorderPane();
		borderPane.setTop(topContent);
		ScrollPane scrollPane = new ScrollPane(tableView);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		scrollPane.setStyle("-fx-background: transparent;");
		borderPane.setCenter(scrollPane);
		BorderPane.setAlignment(tableView, Pos.TOP_CENTER);

		BorderPane.setMargin(tableView, new Insets(10));

		AnimatedBackgroundView fondo = new AnimatedBackgroundView("/images/fondo.jpg");
		StackPane root = new StackPane(fondo, borderPane);

		Scene scene = new Scene(root, 1000, 600);
		scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());

		InactivityTimer timer = new InactivityTimer(primaryStage, () -> {
			Session.idToken = null;
			primaryStage.close();
			new LoginFormView().mostrar(new Stage());
		});
		timer.attachToScene(scene);

		primaryStage.setMaximized(true);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Listado de Criptomonedas");
		primaryStage.show();

		/*
		 * SCHEDULER COMENTADO PARA EVITAR PETICIONES CONSTANTES
		 */
//		scheduler = Executors.newSingleThreadScheduledExecutor();
//		scheduler.scheduleAtFixedRate(() -> {
//		    javafx.application.Platform.runLater(this::fetchCryptoData);
//		}, 60, 60, TimeUnit.SECONDS); // espera 60s y luego repite cada 60s
		// NO ELIMNAR EL SCHEDULER, SOLO COMENTAR

		fetchCryptoData();

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
			Platform.runLater(() -> {
				System.out.println("Datos recibidos: " + list.size() + " criptos.");
				cryptoList.clear();
				for (CryptoMarketDTO crypto : list) {
				    crypto.setFavorito(favoritosGuardados.contains(crypto.getId()));
				    cryptoList.add(crypto);
				}

				tableView.setPrefHeight(cryptoList.size() * 35 + 35); 
			});
		}).exceptionally(e -> {
			System.err.println("Error al obtener criptos: " + e.getMessage());
			e.printStackTrace();
			return null;
		});
	}

	private void parseJson(String responseBody) {
		System.out.println("Entrando a parseJson()");
		System.out.println("Respuesta recibida:\n" + responseBody);

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			List<CryptoMarketDTO> list = mapper.readValue(responseBody, new TypeReference<List<CryptoMarketDTO>>() {
			});
			System.out.println("Criptos parseadas: " + list.size());

			list.forEach(crypto -> {
				System.out.printf("%s (%s) - Precio: %.2f, Cambio: %.2f%%, Volumen: %.0f, MarketCap: %.0f%n",
						crypto.getName(), crypto.getSymbol(), crypto.getCurrentPrice(),
						crypto.getPriceChangePercentage24h(), crypto.getTotalVolume(), crypto.getMarketCap());
			});

			javafx.application.Platform.runLater(() -> {
				System.out.println("Actualizando tabla JavaFX");
				cryptoList.clear();
				cryptoList.addAll(list);
			});

		} catch (IOException e) {
			System.out.println("Error al parsear JSON:");
			e.printStackTrace();
		}
	}

	private List<Double> getHistoricalPrices(String cryptoId) {
		String url = "http://localhost:8080/api/cryptos/" + cryptoId + "/historical";

		if (cryptoId == null || cryptoId.isBlank()) {
			throw new IllegalArgumentException("ID de criptomoneda no puede ser nulo o vacío.");
		}

		System.out.println("Solicitando históricos desde frontend para ID: " + cryptoId);

		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response.body());
			JsonNode prices = root.get("prices");

			System.out.println("📦 JSON de históricos:\n" + response.body());

			if (prices == null || !prices.isArray()) {
				System.err.println("No se encontraron datos históricos para: " + cryptoId);
				return List.of(); 
			}

			List<Double> priceList = new ArrayList<>();
			for (JsonNode point : prices) {
				priceList.add(point.get(1).asDouble()); 
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
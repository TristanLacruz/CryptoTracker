package com.tracker.frontend.views;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.tracker.frontend.AuthContext;

/**
 * Clase que representa la vista del simulador de operaciones para una
 * criptomoneda.
 * Permite al usuario simular la compra o venta de una criptomoneda.
 */
public class SimuladorOperacionView extends VBox {

	private final String cryptoId;
	private final String nombreCrypto;
	private final double precioActual;

	private final ComboBox<String> tipoCombo;
	private final TextField cantidadField;
	private final Label resultadoLabel;

	public SimuladorOperacionView(String cryptoId, String nombreCrypto, double precioActual) {
		this.cryptoId = cryptoId;
		this.nombreCrypto = nombreCrypto;
		this.precioActual = precioActual;
		this.setStyle("-fx-background-color: #1E1E1E;");

		Label lblOperacion = new Label("Simular operación");

		tipoCombo = new ComboBox<>();
		tipoCombo.getItems().addAll("COMPRAR", "VENDER");
		tipoCombo.getStyleClass().add("combo-grande");
		tipoCombo.setValue("COMPRAR");

		cantidadField = new TextField();
		cantidadField.getStyleClass().add("input-grande");
		cantidadField.setPromptText("Cantidad (Crypto)");

		Button ejecutarBtn = new Button("Ejecutar operación");
		resultadoLabel = new Label();

		ejecutarBtn.setOnAction(e -> ejecutarOperacion());

		HBox form = new HBox(10, tipoCombo, cantidadField, ejecutarBtn);
		form.setAlignment(javafx.geometry.Pos.CENTER); // <- AÑADE ESTA LÍNEA

		this.getChildren().addAll(lblOperacion, form, resultadoLabel);
		this.setPadding(new Insets(10));
		this.setSpacing(10);
		this.setAlignment(javafx.geometry.Pos.CENTER);

	}

	private void ejecutarOperacion() {
		String tipo = tipoCombo.getValue();
		String cantidadStr = cantidadField.getText();
		String usuarioId = AuthContext.getInstance().getUsuarioId();

		try {
			double cantidad = Double.parseDouble(cantidadStr);
			if (cantidad <= 0) {
				resultadoLabel.setStyle("-fx-text-fill: red;");
				resultadoLabel.setText("La cantidad debe ser mayor que 0");
				return;
			}

			if (tipo.equals("COMPRAR")) {
				realizarCompra(cryptoId, nombreCrypto, cantidad, precioActual);
			} else {
				realizarVenta(cryptoId, nombreCrypto, cantidad, precioActual);
			}

		} catch (NumberFormatException e) {
			resultadoLabel.setStyle("-fx-text-fill: red;");
			resultadoLabel.setText("Introduce una cantidad válida");
		}
	}

	private void mostrarAlerta(String titulo, String mensaje) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(titulo);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}

	private void realizarCompra(String simbolo, String nombreCrypto, double cantidad, double precio) {
		String usuarioId = AuthContext.getInstance().getUsuarioId();
		String idToken = AuthContext.getInstance().getIdToken();

		JSONObject payload = new JSONObject();
		payload.put("usuarioId", usuarioId);
		payload.put("simbolo", simbolo);
		payload.put("nombreCrypto", nombreCrypto);
		payload.put("cantidadCrypto", cantidad);
		payload.put("precio", precio);

		HttpRequest.Builder builder = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/api/cryptos/comprar"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(payload.toString()));

		if (idToken != null && !idToken.isBlank()) {
			builder.header("Authorization", "Bearer " + idToken);
		}

		HttpRequest request = builder.build();

		HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenAccept(response -> Platform.runLater(() -> {
					int status = response.statusCode();
					String body = response.body();

					if (status != 200) {
						try {
							JSONObject root = new JSONObject(body);
							String mensaje = root.optString("mensaje", "Error desconocido");
							String detalle = root.optString("detalle", "");
							String contenido = mensaje + (!detalle.isBlank() ? "\nDetalles: " + detalle : "");
							mostrarAlertaEstilizada("Compra fallida", contenido, "alerta-error");
						} catch (JSONException e) {
							mostrarAlertaEstilizada("Compra fallida", "Código HTTP: " + status, "alerta-error");
						}
						return;
					}

					if (body == null || body.isBlank()) {
						mostrarAlertaEstilizada("Respuesta vacía", "El servidor no devolvió contenido.",
								"alerta-error");
						return;
					}

					try {
						JSONObject root = new JSONObject(body);
						String estado = root.optString("estado", "error");
						String mensaje = root.optString("mensaje", "");

						if (!"exito".equalsIgnoreCase(estado)) {
							mostrarAlerta("Compra fallida", mensaje);
							return;
						}

						JSONObject det = root.getJSONObject("detalle");
						double cant = det.getDouble("cantidad");
						String sim = det.getString("simbolo");
						double total = det.getDouble("valorTotal");

						mostrarAlertaEstilizada("Compra exitosa",
								String.format("Compraste %.6f %s por %.2f €", cant, sim, total), "alerta-exito");

						actualizarDatosPortafolio();

					} catch (JSONException ex) {
						mostrarAlertaEstilizada("Respuesta inválida", ex.getMessage(), "alerta-error");
					}
				})).exceptionally(ex -> {
					Platform.runLater(() -> mostrarAlertaEstilizada("Error de red", ex.getMessage(), "alerta-error"));
					return null;
				});
	}

	/**
	 * Muestra una alerta estilizada con un mensaje específico.
	 *
	 * @param titulo  El título de la alerta.
	 * @param mensaje El mensaje a mostrar en la alerta.
	 * @param tipoCss El tipo de CSS para aplicar a la alerta (ej. "alerta-error").
	 */
	private void realizarVenta(String simbolo, String nombreCrypto, double cantidad, double precio) {
		String usuarioId = AuthContext.getInstance().getUsuarioId();
		String idToken = AuthContext.getInstance().getIdToken();

		JSONObject payload = new JSONObject();
		payload.put("usuarioId", usuarioId);
		payload.put("simbolo", simbolo);
		payload.put("nombreCrypto", nombreCrypto);
		payload.put("cantidadCrypto", cantidad);
		payload.put("precio", precio);

		HttpRequest.Builder builder = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/api/cryptos/vender"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(payload.toString()));

		if (idToken != null && !idToken.isBlank()) {
			builder.header("Authorization", "Bearer " + idToken);
		}

		HttpRequest request = builder.build();

		HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenAccept(response -> Platform.runLater(() -> {
					int status = response.statusCode();
					String body = response.body();

					if (status != 200 || body == null || body.isBlank()) {
						try {
							JSONObject root = new JSONObject(body);
							String mensaje = root.optString("mensaje", "Error desconocido");
							String detalle = root.optString("detalle", "");
							String contenido = mensaje + (!detalle.isBlank() ? "\nDetalles: " + detalle : "");
							mostrarAlertaEstilizada("Venta fallida", contenido, "alerta-error");
						} catch (JSONException e) {
							mostrarAlertaEstilizada("Venta fallida", "Código HTTP: " + status, "alerta-error");
						}
						return;
					}

					try {
						JSONObject root = new JSONObject(body);
						String estado = root.optString("estado", "error");
						String mensaje = root.optString("mensaje", "");

						if (!"exito".equalsIgnoreCase(estado)) {
							mostrarAlerta("Venta fallida", mensaje);
							return;
						}

						JSONObject detalle = root.getJSONObject("detalle");
						double cant = detalle.getDouble("cantidad");
						String sim = detalle.getString("simbolo");
						double total = detalle.getDouble("valorTotal");

						mostrarAlertaEstilizada("Venta exitosa",
								String.format("Vendiste %.6f %s por %.2f €", cant, sim, total), "alerta-exito");

						actualizarDatosPortafolio();

					} catch (JSONException e) {
						mostrarAlertaEstilizada("Error al procesar respuesta", e.getMessage(), "alerta-error");
					}
				})).exceptionally(ex -> {
					Platform.runLater(() -> mostrarAlertaEstilizada("Error de red", ex.getMessage(), "alerta-error"));
					return null;
				});
	}

	/**
	 * Actualiza los datos del portafolio del usuario.
	 */
	private void actualizarDatosPortafolio() {
		String usuarioId = AuthContext.getInstance().getUsuarioId();
		String idToken = AuthContext.getInstance().getIdToken();

		System.out.println("[CLIENT DEBUG] IdToken=" + idToken);

		String url = "http://localhost:8080/api/portafolios/" + usuarioId;

		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET()
				.header("Authorization", "Bearer " + idToken).build();

		HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenApply(HttpResponse::body).thenAccept(json -> Platform.runLater(() -> {

				})).exceptionally(ex -> {
					Platform.runLater(() -> mostrarAlerta("Error de red", ex.getMessage()));
					return null;
				});
	}

	private void mostrarAlertaEstilizada(String titulo, String mensaje, String tipoCss) {
		Alert alerta = new Alert(Alert.AlertType.NONE);
		alerta.setTitle(titulo);
		alerta.setHeaderText(null);
		alerta.setContentText(mensaje);

		ButtonType ok = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
		alerta.getButtonTypes().setAll(ok);

		DialogPane pane = alerta.getDialogPane();
		pane.getStylesheets().add(getClass().getResource("/css/estilos-alerta.css").toExternalForm());
		pane.getStyleClass().add(tipoCss);

		alerta.showAndWait();
	}

}

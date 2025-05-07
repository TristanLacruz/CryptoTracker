package com.yolo.frontend.views;

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

import org.json.JSONException;
import org.json.JSONObject;

import com.yolo.frontend.AuthContext;

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

		Label lblOperacion = new Label("Simular operación");

		tipoCombo = new ComboBox<>();
		tipoCombo.getItems().addAll("COMPRAR", "VENDER");
		tipoCombo.setValue("COMPRAR");

		cantidadField = new TextField();
		cantidadField.setPromptText("Cantidad en €");

		Button ejecutarBtn = new Button("Ejecutar operación");
		resultadoLabel = new Label();

		ejecutarBtn.setOnAction(e -> ejecutarOperacion());

		HBox form = new HBox(10, tipoCombo, cantidadField, ejecutarBtn);
		this.getChildren().addAll(lblOperacion, form, resultadoLabel);
		this.setPadding(new Insets(10));
		this.setSpacing(10);
	}

	private void ejecutarOperacion() {
		String tipo = tipoCombo.getValue();
		String cantidadStr = cantidadField.getText();
		String usuarioId = AuthContext.getInstance().getUsuarioId();

		try {
			double cantidad = Double.parseDouble(cantidadStr);
			if (cantidad <= 0) {
				resultadoLabel.setStyle("-fx-text-fill: red;");
				resultadoLabel.setText("❌ La cantidad debe ser mayor que 0");
				return;
			}

			if (tipo.equals("COMPRAR")) {
				realizarCompra(cryptoId, nombreCrypto, cantidad, precioActual);
			} else {
				realizarVenta(cryptoId, nombreCrypto, cantidad, precioActual);
			}

		} catch (NumberFormatException e) {
			resultadoLabel.setStyle("-fx-text-fill: red;");
			resultadoLabel.setText("❌ Introduce una cantidad válida");
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

// Montamos el payload JSON
		JSONObject payload = new JSONObject();
		payload.put("usuarioId", usuarioId);
		payload.put("simbolo", simbolo);
		payload.put("nombreCrypto", nombreCrypto);
		payload.put("cantidadCrypto", cantidad);
		payload.put("precio", precio);

// Preparamos la petición
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/cryptos/buy"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(payload.toString()));

// Si quisieras usar autenticación, solo añádela si tienes un token válido:
		String idToken = AuthContext.getInstance().getIdToken();
		if (idToken != null && !idToken.isBlank()) {
			builder.header("Authorization", "Bearer " + idToken);
		}

		HttpRequest request = builder.build();

// Envío asíncrono y manejo de la respuesta
		HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenAccept(response -> Platform.runLater(() -> {
					int status = response.statusCode();
					String body = response.body();

// 1) Código distinto de 200 → mostramos error
					if (status != 200) {
						mostrarAlerta("❌ Compra fallida", "Código HTTP: " + status);
						return;
					}

// 2) Cuerpo vacío → también error
					if (body == null || body.isBlank()) {
						mostrarAlerta("❌ Respuesta vacía", "El servidor no devolvió contenido.");
						return;
					}

// 3) Parseamos el JSON
					try {
						JSONObject root = new JSONObject(body);
						String estado = root.optString("estado", "error");
						String mensaje = root.optString("mensaje", "");

						if (!"exito".equalsIgnoreCase(estado)) {
							// El backend devolvió estado distinto
							mostrarAlerta("❌ Compra fallida", mensaje);
							return;
						}

// Ya es un éxito: extraemos el detalle
						JSONObject det = root.getJSONObject("detalle");
						double cant = det.getDouble("cantidad");
						String sim = det.getString("simbolo");
						double total = det.getDouble("valorTotal");

						mostrarAlerta("✅ Compra exitosa",
								String.format("Compraste %.6f %s por %.2f €", cant, sim, total));

// Actualiza tu UI / datos
						actualizarDatosPortafolio();

					} catch (JSONException ex) {
						mostrarAlerta("❌ Respuesta inválida", ex.getMessage());
					}
				})).exceptionally(ex -> {
					Platform.runLater(() -> mostrarAlerta("❌ Error de red", ex.getMessage()));
					return null;
				});
	}

	private void realizarVenta(String simbolo, String nombreCrypto, double cantidad, double precio) {
		String usuarioId = AuthContext.getInstance().getUsuarioId();

// 1) Preparamos el payload JSON
		JSONObject payload = new JSONObject();
		payload.put("usuarioId", usuarioId);
		payload.put("simbolo", simbolo);
		payload.put("nombreCrypto", nombreCrypto);
		payload.put("cantidadCrypto", cantidad);
		payload.put("precio", precio);

// 2) Construimos la petición
		HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/cryptos/sell"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(payload.toString()));

// 3) Sólo añadimos Authorization si tenemos un token válido
		String idToken = AuthContext.getInstance().getIdToken();
		if (idToken != null && !idToken.isBlank()) {
			builder.header("Authorization", "Bearer " + idToken);
		}

		HttpRequest request = builder.build();

// 4) Envío asíncrono y manejo de la respuesta
		HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenAccept(response -> Platform.runLater(() -> {
					int status = response.statusCode();
					String body = response.body();

// Error HTTP
					if (status != 200) {
						mostrarAlerta("❌ Venta fallida", "Código HTTP: " + status);
						return;
					}

// Respuesta vacía
					if (body == null || body.isBlank()) {
						mostrarAlerta("❌ Respuesta vacía", "El servidor no devolvió contenido.");
						return;
					}

// Parseamos JSON
					try {
						JSONObject root = new JSONObject(body);
						String estado = root.optString("estado", "error");
						String mensaje = root.optString("mensaje", "");

						if (!"exito".equalsIgnoreCase(estado)) {
							mostrarAlerta("❌ Venta fallida", mensaje);
							return;
						}

						JSONObject det = root.getJSONObject("detalle");
						double cant = det.getDouble("cantidad");
						String sim = det.getString("simbolo");
						double total = det.getDouble("valorTotal");

						mostrarAlerta("✅ Venta exitosa",
								String.format("Vendiste %.6f %s por %.2f €", cant, sim, total));
						actualizarDatosPortafolio();

					} catch (JSONException ex) {
						mostrarAlerta("❌ Respuesta inválida", ex.getMessage());
					}
				})).exceptionally(ex -> {
					Platform.runLater(() -> mostrarAlerta("❌ Error de red", ex.getMessage()));
					return null;
				});
	}

	private void actualizarDatosPortafolio() {
		String usuarioId = AuthContext.getInstance().getUsuarioId();

		String url = "http://localhost:8080/api/portafolios/" + usuarioId;

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
				.header("Authorization", "Bearer " + AuthContext.getInstance().getIdToken()).build();

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)
				.thenAccept(json -> Platform.runLater(() -> {
					try {
						if (json == null || json.isBlank() || !json.trim().startsWith("{")) {
							throw new IllegalArgumentException("Respuesta vacía o inválida del backend");
						}

						JSONObject obj = new JSONObject(json);
						double saldo = obj.getDouble("saldo");
						JSONObject criptos = obj.getJSONObject("criptomonedas");
						double cantidadCrypto = criptos.optDouble(cryptoId, 0.0);

						resultadoLabel.setStyle("-fx-text-fill: black;");
						resultadoLabel.setText("💰 Saldo: " + saldo + " € | 🪙 " + cryptoId + ": " + cantidadCrypto);

					} catch (Exception e) {
						resultadoLabel.setStyle("-fx-text-fill: red;");
						resultadoLabel.setText("❌ Error al leer el portafolio: " + e.getMessage());
						System.out.println("❌ Respuesta no válida: " + json); // Para depuración
					}
				}));

	}

}

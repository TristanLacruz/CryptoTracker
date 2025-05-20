package com.tracker.frontend.views;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.tracker.frontend.session.Session;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.frontend.AuthContext;
import com.tracker.frontend.CryptoTableViewApp;

import java.util.HashMap;
import java.util.Map;

public class LoginFormView {

	private static final String API_KEY = "AIzaSyCeFXdnnytQRyuXup6UO3Dj0VX1qXUyCXs";

	public void mostrar(Stage parentStage) {
		Stage stage = new Stage();

		TextField emailField = new TextField();
		PasswordField passwordField = new PasswordField();
		Label resultado = new Label();
		Button btnVolver = new Button("Volver al menú");
		Button btnLogin = new Button("Iniciar sesión");

		VBox content = new VBox(10,
			    new Label("Correo electrónico:"), emailField,
			    new Label("Contraseña:"), passwordField,
			    btnLogin, btnVolver, resultado
			);
			content.setPadding(new Insets(20));
			content.setAlignment(Pos.CENTER);

			// Fondo animado
			AnimatedBackgroundView fondo = new AnimatedBackgroundView("/images/fondo.jpg");

			// StackPane para superponer el fondo y el contenido
			StackPane root = new StackPane(fondo, content);


		btnVolver.setOnAction(ev -> {
			try {
				new MainMenuView().mostrar(parentStage); // Vuelve al menú
				stage.close(); // Cierra la ventana actual
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		btnLogin.setOnAction(e -> {
			String email = emailField.getText();
			String password = passwordField.getText();
			
			// Desactivar botón para evitar múltiples clics
			btnLogin.setDisable(true);
			resultado.setText("⏳ Iniciando sesión...");

			Map<String, String> credenciales = new HashMap<>();
			credenciales.put("email", email);
			credenciales.put("password", password);
			credenciales.put("returnSecureToken", "true");

			try {
				ObjectMapper mapper = new ObjectMapper();
				String json = mapper.writeValueAsString(credenciales);

				HttpRequest request = HttpRequest.newBuilder()
						.uri(URI.create(
								"https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY))
						.header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(json))
						.build();

				HttpClient client = HttpClient.newHttpClient();
				client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body)
						.thenAccept(response -> {
							try {
								//System.out.println("RESPUESTA DE FIREBASE:");
								System.out.println(response);
								Map<String, Object> datos = mapper.readValue(response, Map.class);

								// Verificamos si hay un error
								if (datos.containsKey("error")) {
									Map<String, Object> errorInfo = (Map<String, Object>) datos.get("error");
									String mensajeError = (String) errorInfo.get("message");

									Platform.runLater(() -> {
										resultado.setText("❌ Error: " + mensajeError);
										btnLogin.setDisable(false); // Reactivar botón
									});
									return;
								}

								String idToken = (String) datos.get("idToken");
								String usuarioId = (String) datos.get("localId"); // <- Este es el UID del usuario

								Session.idToken = idToken;
								Session.usuarioId = usuarioId;

								AuthContext.getInstance().setIdToken(idToken);
								AuthContext.getInstance().setUsuarioId(usuarioId);
								
								Platform.runLater(() -> {
									try {
										Session.idToken = idToken;
										Session.usuarioId = usuarioId; // <- Guardamos también el UID
										//System.out.println("✅ ID Token guardado: " + Session.idToken);
										System.out.println("✅ UID guardado: " + Session.usuarioId);

										resultado.setText("✅ Login correcto.");
										stage.close();

										new CryptoTableViewApp().mostrarAppPrincipal(new Stage());
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								});



							} catch (Exception ex) {
								Platform.runLater(() -> {
									resultado.setText("⚠️ Error al procesar la respuesta.");
									btnLogin.setDisable(false); // Reactivar botón
								});
								ex.printStackTrace();
							}
						}).exceptionally(error -> {
							Platform.runLater(() -> {
								resultado.setText("❌ Error de conexión: " + error.getMessage());
								btnLogin.setDisable(false); // Reactivar botón
							});
							error.printStackTrace();
							return null;
						});

			} catch (Exception ex) {
				resultado.setText("❌ Error interno: " + ex.getMessage());
				btnLogin.setDisable(false); // Reactivar botón
				ex.printStackTrace();
			}
		});

		Scene scene = new Scene(root, 400, 400);
		scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm()); // <--- aquí
		stage.setScene(scene);

		stage.setTitle("Iniciar sesión");
		stage.show();
		parentStage.close();
	}
}

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

/**
 * Clase que representa la vista del formulario de inicio de sesión.
 * Permite al usuario ingresar su correo electrónico y contraseña para iniciar
 * sesión.
 */
public class LoginFormView {

	private static final String API_KEY = "AIzaSyCeFXdnnytQRyuXup6UO3Dj0VX1qXUyCXs";

	/**
	 * Muestra la vista del formulario de inicio de sesión.
	 *
	 * @param parentStage La ventana principal desde la cual se abre el formulario
	 *                    de inicio de sesión.
	 */
	public void mostrar(Stage parentStage) {
		Stage stage = new Stage();

		TextField emailField = new TextField();
		emailField.setPrefWidth(300);
		emailField.setMaxWidth(300);
		emailField.getStyleClass().add("text-field");
		PasswordField passwordField = new PasswordField();
		passwordField.setPrefWidth(300);
		passwordField.setMaxWidth(300);
		passwordField.getStyleClass().add("password-field");
		Label resultado = new Label();
		Button btnVolver = new Button("Volver al menú");
		Button btnLogin = new Button("Iniciar sesión");

		VBox content = new VBox(10,
				new Label("Correo electrónico:"), emailField,
				new Label("Contraseña:"), passwordField,
				btnLogin, btnVolver, resultado);
		content.setPadding(new Insets(20));
		content.setAlignment(Pos.CENTER);

		AnimatedBackgroundView fondo = new AnimatedBackgroundView("/images/fondo.jpg");

		StackPane root = new StackPane(fondo, content);

		btnVolver.setOnAction(ev -> {
			try {
				new MainMenuView().mostrar(parentStage);
				stage.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		btnLogin.setOnAction(e -> {
			String email = emailField.getText();
			String password = passwordField.getText();

			btnLogin.setDisable(true);
			resultado.setText("Iniciando sesión...");

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
								System.out.println("Respuesta de Firebase: " + response);
								Map<String, Object> datos = mapper.readValue(response, Map.class);

								if (datos.containsKey("error")) {
									Map<String, Object> errorInfo = (Map<String, Object>) datos.get("error");
									String errorCode = (String) errorInfo.get("message");

									String mensajePersonalizado;
									switch (errorCode) {
										case "EMAIL_NOT_FOUND":
											mensajePersonalizado = "No existe ninguna cuenta con ese correo.";
											break;
										case "INVALID_PASSWORD":
											mensajePersonalizado = "Contraseña incorrecta.";
											break;
										case "USER_DISABLED":
											mensajePersonalizado = "Esta cuenta ha sido desactivada.";
											break;
										case "INVALID_LOGIN_CREDENTIALS":
											mensajePersonalizado = "Correo o contraseña incorrectos.";
											break;
										case "MISSING_PASSWORD":
											mensajePersonalizado = "La contraseña no puede estar vacía.";
											break;
										case "INVALID_EMAIL":
											mensajePersonalizado = "El correo no puede estar vacío.";
											break;
										default:
											mensajePersonalizado = "Error desconocido: " + errorCode;
											break;
									}

									Platform.runLater(() -> {
										resultado.setText("Error: " + mensajePersonalizado);
										btnLogin.setDisable(false);
									});
									return;
								}

								String idToken = (String) datos.get("idToken");
								String usuarioId = (String) datos.get("localId");

								Session.idToken = idToken;
								Session.usuarioId = usuarioId;

								try {
									HttpClient httpClient = HttpClient.newHttpClient();
									ObjectMapper backendMapper = new ObjectMapper();

									HttpRequest checkRequest = HttpRequest.newBuilder()
											.uri(URI.create(
													"http://localhost:8080/api/usuarios/" + usuarioId + "/nombre"))
											.header("Authorization", "Bearer " + idToken)
											.GET()
											.build();

									httpClient.sendAsync(checkRequest, HttpResponse.BodyHandlers.ofString())
											.thenAccept(checkResp -> {
												if (checkResp.statusCode() == 404) {
													try {
														Map<String, String> nuevoUsuario = new HashMap<>();
														nuevoUsuario.put("uid", usuarioId);
														nuevoUsuario.put("email", email);
														nuevoUsuario.put("nombre", email.split("@")[0]);

														String requestBody = backendMapper
																.writeValueAsString(nuevoUsuario);

														HttpRequest crearUsuarioRequest = HttpRequest.newBuilder()
																.uri(URI.create("http://localhost:8080/api/usuarios"))
																.header("Content-Type", "application/json")
																.header("Authorization", "Bearer " + idToken)
																.POST(HttpRequest.BodyPublishers.ofString(requestBody))
																.build();

														httpClient
																.sendAsync(crearUsuarioRequest,
																		HttpResponse.BodyHandlers.ofString())
																.thenAccept(resp -> {
																	System.out.println("Usuario creado en MongoDB: "
																			+ resp.body());
																});
													} catch (Exception ex) {
														System.err.println(
																"Error al crear usuario: " + ex.getMessage());
													}
												} else {
													System.out.println("Usuario ya existe, no se sincroniza.");
												}
											});
								} catch (Exception ex) {
									System.err
											.println("Error al verificar existencia del usuario: " + ex.getMessage());
								}

								AuthContext.getInstance().setIdToken(idToken);
								AuthContext.getInstance().setUsuarioId(usuarioId);

								Platform.runLater(() -> {
									try {
										Session.idToken = idToken;
										Session.usuarioId = usuarioId;
										System.out.println("UID guardado: " + Session.usuarioId);

										resultado.setText("Login correcto.");
										stage.close();

										new CryptoTableViewApp().mostrarAppPrincipal(new Stage());
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								});

							} catch (Exception ex) {
								Platform.runLater(() -> {
									resultado.setText("Error al procesar la respuesta.");
									btnLogin.setDisable(false);
								});
								ex.printStackTrace();
							}
						}).exceptionally(error -> {
							Platform.runLater(() -> {
								resultado.setText("Error de conexión: " + error.getMessage());
								btnLogin.setDisable(false);
							});
							error.printStackTrace();
							return null;
						});
			} catch (Exception ex) {
				resultado.setText("Error interno: " + ex.getMessage());
				btnLogin.setDisable(false);
				ex.printStackTrace();
			}
		});

		Scene scene = new Scene(root, 400, 400);
		scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());
		stage.setScene(scene);

		stage.setTitle("Iniciar sesión");
		stage.setMaximized(true);
		stage.show();
		parentStage.close();
	}
}

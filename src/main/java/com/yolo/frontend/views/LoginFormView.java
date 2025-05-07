package com.yolo.frontend.views;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolo.frontend.AuthContext;
import com.yolo.frontend.CryptoTableViewApp;
import com.yolo.frontend.dto.UsuarioDTO;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginFormView {

    private static final String FIREBASE_API_KEY = "AIzaSyCeFXdnnytQRyuXup6UO3Dj0VX1qXUyCXs"; // ← cambia esto por la real

    public void mostrarLogin(Stage primaryStage) {
        Label lblCorreo = new Label("Correo:");
        TextField txtCorreo = new TextField();

        Label lblClave = new Label("Contraseña:");
        PasswordField txtClave = new PasswordField();

        Button btnLogin = new Button("Iniciar sesión");
        Label lblEstado = new Label();

        btnLogin.setOnAction(e -> {
            String email = txtCorreo.getText();
            String password = txtClave.getText();

            if (email.isBlank() || password.isBlank()) {
                lblEstado.setText("❌ Completa ambos campos");
                return;
            }

            autenticarConFirebase(email, password, primaryStage, lblEstado);
        });

        VBox layout = new VBox(10, lblCorreo, txtCorreo, lblClave, txtClave, btnLogin, lblEstado);
        layout.setPadding(new Insets(20));
        Scene scene = new Scene(layout, 400, 300);

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void autenticarConFirebase(String email, String password, Stage stage, Label lblEstado) {
        try {
            String jsonBody = String.format("""
                {
                  "email": "%s",
                  "password": "%s",
                  "returnSecureToken": true
                }
                """, email, password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> Platform.runLater(() -> {
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode json = mapper.readTree(response);

                            if (json.has("error")) {
                                String mensaje = json.get("error").get("message").asText();
                                lblEstado.setText("❌ Error: " + mensaje);
                            } else {
                                String idToken = json.get("idToken").asText();
                                obtenerUsuarioDesdeBackend(idToken, stage);
                            }
                        } catch (Exception ex) {
                            lblEstado.setText("❌ Error inesperado al procesar respuesta");
                        }
                    }))
                    .exceptionally(e -> {
                        Platform.runLater(() -> lblEstado.setText("❌ Error de red: " + e.getMessage()));
                        return null;
                    });

        } catch (Exception e) {
            lblEstado.setText("❌ Error al construir la solicitud");
        }
    }

    private void obtenerUsuarioDesdeBackend(String idToken, Stage primaryStage) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auth/me/details"))
                .header("Authorization", "Bearer " + idToken)
                .GET()
                .build();

        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> Platform.runLater(() -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        UsuarioDTO usuario = mapper.readValue(json, UsuarioDTO.class);

                        // Guardar en contexto
                        AuthContext.getInstance().setUsuario(usuario);
                        AuthContext.getInstance().setUsuarioId(usuario.getId());
                        AuthContext.getInstance().setIdToken(idToken);

                        System.out.println("✅ Login correcto: " + usuario.getEmail());
                        new CryptoTableViewApp().mostrarAppPrincipal(primaryStage);

                    } catch (Exception e) {
                        mostrarAlerta("Error al procesar usuario", e.getMessage());
                    }
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> mostrarAlerta("Error al obtener usuario", e.getMessage()));
                    return null;
                });
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

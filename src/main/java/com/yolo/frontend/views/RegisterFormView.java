package com.yolo.frontend.views;

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

public class RegisterFormView {

    public void mostrarRegistro(Stage stage) {
        Label lblTitulo = new Label("Registro de Usuario");

        TextField emailField = new TextField();
        emailField.setPromptText("Correo electrónico");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Contraseña");

        Button registrarBtn = new Button("Registrarse");
        Label mensajeLabel = new Label();

        registrarBtn.setOnAction(e -> {
            String email = emailField.getText();
            String pass = passField.getText();

            if (email.isEmpty() || pass.isEmpty()) {
                mensajeLabel.setStyle("-fx-text-fill: red;");
                mensajeLabel.setText("❌ Todos los campos son obligatorios");
                return;
            }

            registrarEnFirebase(email, pass, mensajeLabel, stage);
        });

        VBox layout = new VBox(10, lblTitulo, emailField, passField, registrarBtn, mensajeLabel);
        layout.setPadding(new Insets(20));
        stage.setScene(new Scene(layout, 400, 250));
        stage.setTitle("Registro");
        stage.show();
    }

    private void registrarEnFirebase(String email, String password, Label mensajeLabel, Stage stage) {
        String json = String.format("""
        {
          "email": "%s",
          "password": "%s",
          "returnSecureToken": true
        }
        """, email, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=TU_API_KEY"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> Platform.runLater(() -> {
                    if (response.contains("error")) {
                        mensajeLabel.setStyle("-fx-text-fill: red;");
                        mensajeLabel.setText("❌ Error: " + extraerMensajeError(response));
                        return;
                    }

                    String idToken = extraerCampo(response, "idToken");
                    obtenerDatosUsuario(idToken, stage);
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        mensajeLabel.setStyle("-fx-text-fill: red;");
                        mensajeLabel.setText("❌ Error al registrar: " + e.getMessage());
                    });
                    return null;
                });
    }

    private void obtenerDatosUsuario(String idToken, Stage stage) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/auth/me/details"))
                .header("Authorization", "Bearer " + idToken)
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(json -> Platform.runLater(() -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        UsuarioDTO usuario = mapper.readValue(json, UsuarioDTO.class);

                        AuthContext.getInstance().setUsuario(usuario);
                        AuthContext.getInstance().setUsuarioId(usuario.getId());
                        AuthContext.getInstance().setIdToken(idToken);

                        System.out.println("✅ Usuario registrado y logueado: " + usuario.getEmail());

                        CryptoTableViewApp app = new CryptoTableViewApp();
                        app.mostrarAppPrincipal(stage);

                    } catch (Exception e) {
                        mostrarAlerta("Error al procesar usuario", e.getMessage());
                    }
                }))
                .exceptionally(e -> {
                    Platform.runLater(() -> mostrarAlerta("Error al obtener usuario", e.getMessage()));
                    return null;
                });
    }

    private String extraerCampo(String json, String campo) {
        int start = json.indexOf(campo + "\":\"") + campo.length() + 3;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    private String extraerMensajeError(String response) {
        if (response.contains("EMAIL_EXISTS")) return "El correo ya está registrado";
        return "Registro fallido";
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

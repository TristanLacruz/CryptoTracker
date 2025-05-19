package com.tracker.frontend.views;

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

import com.fasterxml.jackson.databind.ObjectMapper;
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

        VBox root = new VBox(10,
                new Label("Correo electrónico:"), emailField,
                new Label("Contraseña:"), passwordField,
                btnLogin,
                btnVolver,
                resultado
        );
        root.setPadding(new Insets(20));

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

            Map<String, String> credenciales = new HashMap<>();
            credenciales.put("email", email);
            credenciales.put("password", password);
            credenciales.put("returnSecureToken", "true");

            try {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(credenciales);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpClient client = HttpClient.newHttpClient();
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body)
                        .thenAccept(response -> {
                            try {
                                Map<String, Object> datos = mapper.readValue(response, Map.class);
                                String idToken = (String) datos.get("idToken");

                                Platform.runLater(() -> {
                                    resultado.setText("✅ Login correcto.");
                                    stage.close();

                                    try {
                                        // Guarda el token en sesión si lo necesitas globalmente
                                        Session.idToken = idToken;

                                        // Abre la vista principal con la lista de criptomonedas
                                        new CryptoTableViewApp().start(stage);// O el nombre real de tu clase de vista principal
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                });


                            } catch (Exception ex) {
                                Platform.runLater(() -> resultado.setText("⚠️ Error al procesar respuesta: " + ex.getMessage()));
                            }
                        })
                        .exceptionally(error -> {
                            Platform.runLater(() -> resultado.setText("❌ Error de login: " + error.getMessage()));
                            error.printStackTrace();
                            return null;
                        });

            } catch (Exception ex) {
                resultado.setText("❌ Error interno: " + ex.getMessage());
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

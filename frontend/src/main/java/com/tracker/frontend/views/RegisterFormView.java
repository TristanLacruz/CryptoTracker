package com.tracker.frontend.views;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.http.*;
import java.net.URI;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.frontend.CryptoTableViewApp;

/**
 * Clase que representa la vista del formulario de registro.
 * Permite al usuario ingresar su información para crear una nueva cuenta.
 */
public class RegisterFormView {

    /**
     * Muestra la vista del formulario de registro.
     *
     * @param parentStage La ventana principal desde la cual se abre el formulario de registro.
     */
    public void mostrar(Stage parentStage) {
        Stage stage = new Stage();

        TextField nombreUsuario = new TextField();
        nombreUsuario.setPrefWidth(300);
        nombreUsuario.setMaxWidth(300);
        nombreUsuario.getStyleClass().add("text-field");

        TextField email = new TextField();
        email.setPrefWidth(300);
        email.setMaxWidth(300);
        email.getStyleClass().add("text-field");

        TextField nombre = new TextField();
        nombre.setPrefWidth(300);
        nombre.setMaxWidth(300);
        nombre.getStyleClass().add("text-field");

        TextField apellido = new TextField();
        apellido.setPrefWidth(300);
        apellido.setMaxWidth(300);
        apellido.getStyleClass().add("text-field");

        PasswordField password = new PasswordField();
        password.setPrefWidth(300);
        password.setMaxWidth(300);
        password.getStyleClass().add("text-field");

        Button btnVolver = new Button("Volver al menú");
        Button btnRegistrar = new Button("Registrarse");
        Label mensaje = new Label();

        VBox content = new VBox(10,
            new Label("Nombre de usuario:"), nombreUsuario,
            new Label("Correo electrónico:"), email,
            new Label("Nombre:"), nombre,
            new Label("Apellido:"), apellido,
            new Label("Contraseña:"), password,
            btnRegistrar, btnVolver, mensaje
        );
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        AnimatedBackgroundView fondo = new AnimatedBackgroundView("/images/fondo.jpg");
        StackPane root = new StackPane(fondo, content);
        root.setPadding(new Insets(20));

        btnVolver.setOnAction(ev -> {
            try {
                new MainMenuView().mostrar(parentStage);
                stage.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        btnRegistrar.setOnAction(e -> {
            try {
                Map<String, String> datos = new HashMap<>();
                datos.put("nombreUsuario", nombreUsuario.getText());
                datos.put("email", email.getText());
                datos.put("nombre", nombre.getText());
                datos.put("apellido", apellido.getText());
                datos.put("contrasena", password.getText());
                datos.put("rol", "USER");

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(datos);

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/usuarios"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

                HttpClient client = HttpClient.newHttpClient();
                client.sendAsync(request, BodyHandlers.ofString())
                    .thenApply(response -> {
                        int status = response.statusCode();
                        String body = response.body();

                        if (status >= 200 && status < 300) {
                            Platform.runLater(() -> {
                                mensaje.setText("Usuario registrado correctamente.");
                                stage.close();
                                try {
                                	new CryptoTableViewApp().mostrarAppPrincipal(new Stage());
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    mensaje.setText("No se pudo abrir la ventana principal.");
                                }
                            });
                        } else {
                            Platform.runLater(() -> {
                                String mensajePersonalizado = "Error desconocido al registrar el usuario.";
                                try {
                                    ObjectMapper mapperError = new ObjectMapper();
                                    Map<String, Object> errorJson = mapperError.readValue(body, Map.class);

                                    Object mensajeRaw = errorJson.get("message");
                                    if (mensajeRaw != null) {
                                        String mensajeBackend = mensajeRaw.toString();

                                        switch (mensajeBackend) {
                                            case "EMAIL_EXISTS":
                                                mensajePersonalizado = "Ya existe una cuenta con este correo electrónico.";
                                                break;
                                            case "WEAK_PASSWORD":
                                                mensajePersonalizado = "La contraseña debe tener al menos 6 caracteres.";
                                                break;
                                            case "INVALID_EMAIL":
                                                mensajePersonalizado = "El formato del correo es inválido.";
                                                break;
                                            default:
                                                mensajePersonalizado = "Error: " + mensajeBackend;
                                                break;
                                        }
                                    }
                                } catch (Exception ex) {
                                    System.err.println("No se pudo leer el mensaje de error: " + ex.getMessage());
                                }

                                mensaje.setText(mensajePersonalizado);
                            });
                        }
                        return null;
                    })
                    .exceptionally(error -> {
                        Platform.runLater(() -> mensaje.setText("Error de conexión: " + error.getMessage()));
                        error.printStackTrace();
                        return null;
                    });

            } catch (Exception ex) {
                mensaje.setText("Error interno.");
                ex.printStackTrace();
            }
        });

        Scene scene = new Scene(root, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("Formulario de Registro");
        stage.show();
        parentStage.close();
    }
}

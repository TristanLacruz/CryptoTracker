package com.yolo.frontend.views;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.http.*;
import java.net.URI;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RegisterFormView {

	public void mostrar(Stage parentStage) {
		Stage stage = new Stage();

		TextField nombreUsuario = new TextField();
		TextField email = new TextField();
		TextField nombre = new TextField();
		TextField apellido = new TextField();
		PasswordField password = new PasswordField();

		Button btnVolver = new Button("Volver al menú");
		Button btnRegistrar = new Button("Registrarse");
		Label mensaje = new Label();

		VBox root = new VBox(10, new Label("Nombre de usuario:"), nombreUsuario, new Label("Correo electrónico:"),
				email, new Label("Nombre:"), nombre, new Label("Apellido:"), apellido, new Label("Contraseña:"),
				password, btnRegistrar, btnVolver, mensaje);
        root.setPadding(new Insets(20));
        
		btnVolver.setOnAction(ev -> {
        	try {
        		new MainMenuView().start(parentStage); // Vuelve al menú
        		stage.close(); // Cierra la ventana actual
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

				HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/api/usuarios"))
						.header("Content-Type", "application/json")
						.POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8)).build();

				HttpClient client = HttpClient.newHttpClient();
				client.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body)
				.thenAccept(response -> {
				    Platform.runLater(() -> {
				        mensaje.setText("✅ Usuario registrado correctamente.");
				        // Cierra ventana de registro
				        stage.close();
				        try {
				            // Cargar y mostrar la nueva vista principal
				            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/main/MainView.fxml"));
				            Parent mainRoot = loader.load();  // 👈 cambio aquí
				            Stage mainStage = new Stage();
				            mainStage.setScene(new Scene(mainRoot));
				            mainStage.setTitle("CryptoTracker - Panel principal");
				            mainStage.show();
				        } catch (Exception ex) {
				            ex.printStackTrace();
				            mensaje.setText("❌ No se pudo abrir la ventana principal.");
				        }
				    });
				});


			} catch (Exception ex) {
				mensaje.setText("❌ Error interno.");
				ex.printStackTrace();
			}
		});

		Scene scene = new Scene(root, 400, 400);
		scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm()); // <--- aquí
		stage.setScene(scene);

		stage.setTitle("Formulario de Registro");
		stage.show();
		parentStage.close();
	}
}

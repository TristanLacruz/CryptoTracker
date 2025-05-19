package com.tracker.frontend.views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuView {

    public void mostrar(Stage primaryStage) {
        // Botones
        Button btnLogin = new Button("Iniciar sesión");
        Button btnRegister = new Button("Registrarse");

        // Acción del botón "Iniciar sesión"
        btnLogin.setOnAction(e -> {
            System.out.println("🔵 Botón 'Iniciar sesión' pulsado");
            try {
                new LoginFormView().mostrar(primaryStage);
            } catch (Exception ex) {
                System.out.println("❌ Error al abrir LoginFormView");
                ex.printStackTrace();
            }
        });

        // Acción del botón "Registrarse"
        btnRegister.setOnAction(e -> {
            System.out.println("🟢 Botón 'Registrarse' pulsado");
            try {
                new RegisterFormView().mostrar(primaryStage);
            } catch (Exception ex) {
                System.out.println("❌ Error al abrir RegisterFormView");
                ex.printStackTrace();
            }
        });

        // Layout
        VBox root = new VBox(20, btnRegister, btnLogin);
        root.setStyle("-fx-background-color: #1E1E1E;");
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 400, 400);
        scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());

        primaryStage.setTitle("CryptoTracker - Menú Principal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

package com.tracker.frontend.views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenuView {

	public void mostrar(Stage primaryStage) {
	    double width = 1000;
	    double height = 600;

	    AnimatedBackgroundView bg = new AnimatedBackgroundView("/images/fondo.jpg");

	    Button btnLogin = new Button("Iniciar sesión");
	    Button btnRegister = new Button("Registrarse");

	    btnLogin.setOnAction(e -> {
	        new LoginFormView().mostrar(primaryStage);
	    });

	    btnRegister.setOnAction(e -> {
	        new RegisterFormView().mostrar(primaryStage);
	    });

	    VBox buttonLayout = new VBox(20, btnRegister, btnLogin);
	    buttonLayout.setAlignment(Pos.CENTER);

	    StackPane root = new StackPane(bg, buttonLayout);

	    Scene scene = new Scene(root, width, height);
	    scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());

	    primaryStage.setTitle("CryptoTracker - Menú Principal");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
}

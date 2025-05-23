package com.tracker.frontend.views;

import com.tracker.frontend.util.InactivityTimer;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Clase que representa la vista del menú principal de la aplicación.
 * Permite al usuario iniciar sesión o registrarse.
 */
public class MainMenuView {

	public void mostrar(Stage primaryStage) {
		Label titulo = new Label("CryptoTracker");
		titulo.getStyleClass().add("titulo-app");

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

	    VBox buttonLayout = new VBox(20, titulo, btnRegister, btnLogin);
	    buttonLayout.setAlignment(Pos.CENTER);
	    buttonLayout.setTranslateY(-80); 
	    
	    StackPane root = new StackPane(bg, buttonLayout);

	    Scene scene = new Scene(root, width, height);
	    	        
	    scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());

	    primaryStage.setTitle("CryptoTracker - Menú Principal");
	    primaryStage.setMaximized(true); 
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}
}

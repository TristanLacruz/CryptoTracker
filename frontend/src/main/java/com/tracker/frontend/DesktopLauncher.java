package com.tracker.frontend;

import com.tracker.frontend.views.MainMenuView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Clase principal para lanzar la aplicación de escritorio.
 * Extiende la clase Application de JavaFX.
 */
public class DesktopLauncher extends Application {

    /**
     * Método principal que inicia la aplicación.
     *
     * @param primaryStage La ventana principal de la aplicación.
     */
    @Override
    public void start(Stage primaryStage) {
        new MainMenuView().mostrar(primaryStage);
    }

    /**
     * Método principal que lanza la aplicación.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
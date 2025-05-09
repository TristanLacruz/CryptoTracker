package com.yolo.frontend;

import com.yolo.backend.YoloAppApplication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;

public class DesktopLauncher extends Application {

    private static final String[] SPRING_ARGS = {};

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 1) Arranca Spring Boot en un hilo aparte
        new Thread(() -> SpringApplication.run(YoloAppApplication.class, SPRING_ARGS))
            .setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
        // (Opcional) espera a que el contexto de Spring esté listo
        Thread.sleep(2_000);

        // 2) Carga tu escena JavaFX
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/main-view.fxml"));
        primaryStage.setTitle("CryptoTracker");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Pásale aquí cualquier argumento que necesite Spring
        launch(args);
    }
}

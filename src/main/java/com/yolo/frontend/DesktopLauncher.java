package com.yolo.frontend;

import com.yolo.backend.YoloAppApplication;
import com.yolo.frontend.views.MainMenuView;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;

public class DesktopLauncher extends Application {

    private static final String[] SPRING_ARGS = {};

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Arrancar Spring Boot en un hilo separado
        Thread springThread = new Thread(() -> SpringApplication.run(YoloAppApplication.class, SPRING_ARGS));
        springThread.setDaemon(true); // permite que se cierre la app al cerrar la ventana
        springThread.start();

        // Opcional: pequeña espera para asegurar que Spring arranca
        Thread.sleep(2000);

        // Mostrar la vista principal (menú)
        new MainMenuView().mostrar(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

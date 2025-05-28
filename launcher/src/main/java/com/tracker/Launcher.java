package com.tracker;

import java.io.File;

public class Launcher {
    public static void main(String[] args) {
        try {
            // Ruta base desde donde se ejecutan los JARs
            String basePath = "."; // o "app" si los mueves a una subcarpeta
            String javafxPath = "C:\\Program Files\\javafx-sdk-21.0.6\\lib"; // actualiza si usas otra ruta

            // Lanzar backend
            System.out.println("Iniciando backend...");
            ProcessBuilder backend = new ProcessBuilder(
                "java", "-jar", "backend-0.0.1-SNAPSHOT.jar"
            );
            backend.directory(new File(basePath));
            backend.inheritIO();
            backend.start();

            // Esperar unos segundos antes de lanzar el frontend
            Thread.sleep(3000);

            // Lanzar frontend con JavaFX
            System.out.println("Iniciando frontend...");
            ProcessBuilder frontend = new ProcessBuilder(
                "java",
                "--module-path", javafxPath,
                "--add-modules", "javafx.controls,javafx.fxml",
                "-jar", "frontend-0.0.1-SNAPSHOT-jar-with-dependencies.jar"
            );
            frontend.directory(new File(basePath));
            frontend.inheritIO();
            frontend.start();

        } catch (Exception e) {
            System.err.println("❌ Error al lanzar la aplicación:");
            e.printStackTrace();
        }
    }
}

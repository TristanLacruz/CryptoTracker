package com.tracker;

import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class Launcher {
    public static void main(String[] args) {
        try {
            // Ruta base de ejecución
            String basePath = "app";
            String javafxPath = "C:\\javafx-sdk-21.0.7\\lib"; 
            
            // Lanzar backend
            System.out.println("Iniciando backend...");
            ProcessBuilder backend = new ProcessBuilder("java", "-jar", "backend.jar");
            backend.directory(new File(basePath));
            backend.inheritIO();
            backend.start();

            // Esperar a que arranque
            Thread.sleep(3000);

            // Lanzar frontend con JavaFX
            System.out.println("Iniciando frontend...");
            ProcessBuilder frontend = new ProcessBuilder(
                "java",
                "--module-path", javafxPath,
                "--add-modules", "javafx.controls,javafx.fxml",
                "-jar", "frontend.jar"
            );
            frontend.directory(new File(basePath));
            frontend.inheritIO();
            frontend.start();

        } catch (Exception e) {
            System.err.println("Error al lanzar la aplicación:");
            e.printStackTrace();
        }
    }
}

package com.tracker;

import java.io.File;

/**
 * Clase principal para iniciar el backend y frontend de la aplicación.
 * Asegúrate de que los archivos JAR del backend y frontend estén en el mismo directorio que este launcher.
 */
public class Launcher {

    /**
     * Método principal que inicia el backend y frontend de la aplicación.
     * Asegúrate de que los archivos JAR del backend y frontend estén en el mismo directorio que este launcher.
     *
     * @param args Argumentos de línea de comandos (no se utilizan en este launcher).
     */
    public static void main(String[] args) {
        try {
            String basePath = "."; 
            String javafxPath = "C:\\Program Files\\javafx-sdk-21.0.6\\lib";

            System.out.println("Iniciando backend...");
            ProcessBuilder backend = new ProcessBuilder(
                "java", "-jar", "backend-0.0.1-SNAPSHOT.jar"
            );
            backend.directory(new File(basePath));
            backend.inheritIO();
            backend.start();

            Thread.sleep(3000);

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
            System.err.println("Error al lanzar la aplicación:");
            e.printStackTrace();
        }
    }
}

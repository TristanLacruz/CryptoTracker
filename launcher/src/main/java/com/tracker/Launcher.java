package com.tracker;

import java.io.File;

public class Launcher {
    public static void main(String[] args) {
        try {
        	// Abrir proceso de backend ejecutado en la carpeta app
            System.out.println("Iniciando backend...");
            ProcessBuilder backend = new ProcessBuilder("java", "-jar", "app/backend.jar");
            backend.inheritIO();
            backend.start();

            // Esperar 3 segundos para que el backend inicie
            Thread.sleep(3000);

            // Abrir proceso de frontend ejecutado en la carpeta app
            System.out.println("Iniciando frontend...");
            ProcessBuilder frontend = new ProcessBuilder("java", "-jar", "app/frontend.jar");
            frontend.inheritIO(); // Hereda la salida del proceso padre
            frontend.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
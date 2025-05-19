package com.yolo.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import com.yolo.frontend.views.MainMenuView;

@SpringBootApplication
@EnableMethodSecurity
@EnableCaching
public class YoloAppApplication {

	public static void main(String[] args) {
		// Lanza Spring Boot en un hilo separado
		Thread backendThread = new Thread(() -> SpringApplication.run(YoloAppApplication.class, args));
		backendThread.setDaemon(true); // Para que se cierre cuando termine la app
		backendThread.start();

		// Lanza la app JavaFX (menú)
		MainMenuView.main(args);
	}
}

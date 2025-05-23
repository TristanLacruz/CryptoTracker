package com.tracker.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    /**
     * Configura Firebase utilizando las credenciales del archivo JSON.
     * Este método se ejecuta al iniciar la aplicación.
     *
     * @throws IOException si no se puede leer el archivo de credenciales
     */
    @PostConstruct
    public void init() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(
                        new ClassPathResource("cryptotracker-717d2-firebase-adminsdk-fbsvc-2832f35a8b.json").getInputStream()
                ))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}

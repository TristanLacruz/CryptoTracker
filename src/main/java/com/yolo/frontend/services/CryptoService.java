package com.yolo.frontend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolo.frontend.dto.CryptoMarketDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CryptoService {

    private static final String API_URL = "http://localhost:8080/api/cryptos/market";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static CompletableFuture<List<CryptoMarketDTO>> getMarketData() {
        // Paso 1: Codificar usuario y contraseña en Base64
        String user = "admin";
        String password = "admin";
        String encodedAuth = java.util.Base64.getEncoder()
                .encodeToString((user + ":" + password).getBytes());

        // Paso 2: Crear la solicitud con el header Authorization
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Basic " + encodedAuth)
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return response.body();
                    } else {
                        throw new RuntimeException("Error en la respuesta: " + response.statusCode());
                    }
                })
                .thenApply(body -> {
                    try {
                        return mapper.readValue(body, new TypeReference<List<CryptoMarketDTO>>() {});
                    } catch (Exception e) {
                        throw new RuntimeException("Error al parsear JSON", e);
                    }
                });
    }

}

package com.tracker.frontend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.common.dto.CryptoMarketDTO;
import com.tracker.frontend.AuthContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
 * Clase de servicio para obtener datos del mercado de criptomonedas.
 * Utiliza una API REST para obtener información sobre criptomonedas.
 */
public class CryptoService {

    private static final String API_URL = "http://localhost:8080/api/cryptos/market";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    /**
     * Obtiene los datos del mercado de criptomonedas.
     * @return Una lista de objetos CryptoMarketDTO.
     */
    public static CompletableFuture<List<CryptoMarketDTO>> getMarketData() {
        String idToken = AuthContext.getInstance().getIdToken();

        if (idToken == null || idToken.isBlank()) {
            throw new RuntimeException("Token no disponible para autenticar la solicitud.");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + idToken) 
                .header("Accept", "application/json")
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return response.body();
                    } else {
                        throw new RuntimeException("Error en la respuesta: HTTP " + response.statusCode());
                    }
                })
                .thenApply(body -> {
                    try {
                        return mapper.readValue(body, new TypeReference<List<CryptoMarketDTO>>() {
                        });
                    } catch (Exception e) {
                        throw new RuntimeException("Error al parsear JSON", e);
                    }
                });
    }

}

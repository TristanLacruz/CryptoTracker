package com.yolo.frontend.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class OperacionRestClient {

    private static final String BASE_URL = "http://localhost:8080/api/transacciones";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static CompletableFuture<String> ejecutarOperacion(String usuarioId, String cryptoId, String tipo, double cantidadUSD) {
        if (cantidadUSD <= 0) {
            return CompletableFuture.completedFuture("❌ La cantidad debe ser mayor que 0");
        }

        String endpoint = tipo.equalsIgnoreCase("COMPRAR") ? "/comprar" : "/vender";
        String url = BASE_URL + endpoint +
                "?usuarioId=" + usuarioId +
                "&simbolo=" + cryptoId +
                "&cantidadUSD=" + cantidadUSD;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        return "✅ Operación exitosa: " + response.body();
                    } else {
                        return "❌ Error: " + response.body();
                    }
                })
                .exceptionally(ex -> "❌ Error al enviar la operación: " + ex.getMessage());
    }
} 

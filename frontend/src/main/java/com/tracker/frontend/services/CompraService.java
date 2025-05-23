package com.tracker.frontend.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.common.dto.CompraRequestDTO;

public class CompraService {

    private static final String API_URL = "http://localhost:8080/api/transacciones/comprar";

    /**
     * Realiza una compra de criptomonedas.
     *
     * @param usuarioId El ID del usuario que realiza la compra.
     * @param simbolo   El símbolo de la criptomoneda a comprar.
     * @param valor     La cantidad de criptomonedas a comprar.
     */
    public void comprarCripto(String usuarioId, String simbolo, double valor) {
        try {
            // Crear el objeto DTO
            CompraRequestDTO compra = new CompraRequestDTO();
            compra.setUsuarioId(usuarioId);
            compra.setSimbolo(simbolo);
            compra.setCantidadCrypto(valor);

            compra.setNombreCrypto(simbolo.toUpperCase());
            
            // Convertir el DTO a JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(compra);

            // Crear el cliente y la petición
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Enviar y manejar la respuesta
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Código de respuesta: " + response.statusCode());
            System.out.println("Respuesta: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

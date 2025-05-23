package com.tracker.frontend.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper; 
import com.tracker.common.dto.VentaRequestDTO;

public class VentaService {

	    private static final String API_URL = "http://localhost:8080/api/transacciones/comprar";

		/**
	     * Realiza una venta de criptomonedas.
	     *
	     * @param usuarioId El ID del usuario que realiza la venta.
	     * @param simbolo   El símbolo de la criptomoneda a vender.
	     * @param valor     La cantidad de criptomonedas a vender.
	     */				
	    public void venderCripto(String usuarioId, String simbolo, double valor) {
	        try {
	            // Crear el objeto DTO
	            VentaRequestDTO venta = new VentaRequestDTO();
	            venta.setUsuarioId(usuarioId);
	            venta.setSimbolo(simbolo);
	            venta.setCantidadCrypto(valor);

	            // Convertir el DTO a JSON
	            ObjectMapper objectMapper = new ObjectMapper();
	            String requestBody = objectMapper.writeValueAsString(venta);

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

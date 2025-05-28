package com.tracker.frontend.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Clase para manejar el almacenamiento de favoritos en un archivo JSON.
 */
public class FavoritosStorage {
    private static final String FILE_PATH = "src/main/resources/favoritos.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Carga los favoritos de un usuario desde el archivo.
     */
    public static Set<String> cargarFavoritos(String usuarioId) {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists())
                return new HashSet<>();
            Map<String, Set<String>> map = mapper.readValue(file, new TypeReference<Map<String, Set<String>>>() {
            });
            return map.getOrDefault(usuarioId, new HashSet<>());
        } catch (Exception e) {
            System.err.println("Error al leer favoritos: " + e.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * Guarda los favoritos de un usuario en el archivo.
     */
    public static void guardarFavoritos(String usuarioId, Set<String> favoritos) {
        try {
            File file = new File(FILE_PATH);
            Map<String, Set<String>> map;

            if (file.exists()) {
                map = mapper.readValue(file, new TypeReference<Map<String, Set<String>>>() {
                });
            } else {
                map = new HashMap<>();
            }

            map.put(usuarioId, favoritos);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, map);
        } catch (Exception e) {
            System.err.println("Error al guardar favoritos: " + e.getMessage());
        }
    }
}

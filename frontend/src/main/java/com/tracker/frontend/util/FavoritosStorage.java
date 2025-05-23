package com.tracker.frontend.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase para manejar el almacenamiento de favoritos en un archivo JSON.
 */
public class FavoritosStorage {
    private static final String FILE_PATH = "favoritos.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Carga los favoritos desde un archivo JSON.
     *
     * @return Un conjunto de cadenas que representan los favoritos.
     */
    public static Set<String> cargarFavoritos() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return new HashSet<>();
            return mapper.readValue(file, new TypeReference<Set<String>>() {});
        } catch (Exception e) {
            System.err.println("Error al leer favoritos: " + e.getMessage());
            return new HashSet<>();
        }
    }

    /**
     * Guarda los favoritos en un archivo JSON.
     *
     * @param favoritos Un conjunto de cadenas que representan los favoritos.
     */
    public static void guardarFavoritos(Set<String> favoritos) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), favoritos);
        } catch (Exception e) {
            System.err.println("Error al guardar favoritos: " + e.getMessage());
        }
    }
}

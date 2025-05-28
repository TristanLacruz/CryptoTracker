package com.tracker.backend.mvc.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Map;

/**
 * Manejador global de excepciones para la aplicación.
 * Captura y maneja excepciones comunes, proporcionando respuestas estandarizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de tipo ResponseStatusException.
     * 
     * @param ex la excepción capturada
     * @return una respuesta con el estado y mensaje de error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "estado", "error",
                    "mensaje", "Error en la operación",
                    "detalle", ex.getMessage()
                ));
    }

    /**
     * Maneja excepciones de tipo HttpMessageNotReadableException.
     * 
     * @param ex la excepción capturada
     * @return una respuesta con el estado y mensaje de error
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "estado", "error",
                    "mensaje", "El cuerpo de la solicitud no es válido",
                    "detalle", ex.getMostSpecificCause().getMessage()
                ));
    }

    /**
     * Maneja excepciones de tipo MethodArgumentTypeMismatchException.
     * 
     * @param ex la excepción capturada
     * @return una respuesta con el estado y mensaje de error
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "estado", "error",
                    "mensaje", "Tipo de parámetro incorrecto",
                    "detalle", ex.getMessage()
                ));
    }

    /**
     * Maneja excepciones de tipo CriptomonedaNoEncontradaException.
     * 
     * @param ex la excepción capturada
     * @return una respuesta con el estado y mensaje de error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "estado", "error",
                    "mensaje", "Error interno en el servidor",
                    "detalle", ex.getMessage()
                ));
    }
}

package com.tracker.backend.mvc.model;

/**
 * Clase que representa una solicitud de autenticación.
 * Contiene los campos necesarios para autenticar a un usuario.
 */
public class AuthRequest {
    private String email;
    private String password;

    public AuthRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
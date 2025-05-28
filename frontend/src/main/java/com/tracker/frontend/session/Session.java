package com.tracker.frontend.session;

/**
 * Clase Session que almacena el ID del token y el ID del usuario.
 * Esta clase es utilizada para mantener la sesión del usuario en la aplicación.
 */
public class Session {

    public static String idToken;
    public static String usuarioId;
    
    public static String getIdToken() {
        return idToken;
    }

    public static String getUsuarioId() {
        return usuarioId;
    }
}

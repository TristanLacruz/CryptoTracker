package com.tracker.frontend;

import com.tracker.common.dto.UsuarioDTO;

/**
 * Clase que representa el contexto de autenticación del usuario.
 * Utilizada para almacenar información relacionada con la sesión del usuario.
 */
public class AuthContext {

    private static AuthContext instancia;
    private String idToken;
    private String usuarioId;
    private UsuarioDTO usuario;

    private AuthContext() {}

    public static AuthContext getInstance() {
        if (instancia == null) {
            instancia = new AuthContext();
        }
        return instancia;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public UsuarioDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDTO usuario) {
        this.usuario = usuario;
    }
}

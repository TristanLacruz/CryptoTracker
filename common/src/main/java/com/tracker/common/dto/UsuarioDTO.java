package com.tracker.common.dto;

public class UsuarioDTO {
    private String uid;
    private String email;

    public UsuarioDTO() {
    }

    public UsuarioDTO(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

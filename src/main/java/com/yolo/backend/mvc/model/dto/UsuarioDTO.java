package com.yolo.backend.mvc.model.dto;

import com.yolo.backend.mvc.model.entity.Usuario;

public class UsuarioDTO {

    private String id;
    private String uid;
    private String email;
    private String nombreUsuario;
    private String rol;
    private Double saldo;

    public UsuarioDTO() {}

    public UsuarioDTO(Usuario u) {
        this.id = u.getId();
        this.uid = u.getUid();
        this.email = u.getEmail();
        this.nombreUsuario = u.getNombreUsuario();
        this.rol = u.getRol();
        this.saldo = u.getSaldo();
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }
}

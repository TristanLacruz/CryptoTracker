package com.yolo.backend.mvc.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Document(collection = "usuarios")
public class Usuario implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String nombreUsuario;
	private String email;
	private String nombre;
	private String apellido;
	private String contrasena;
	private String rol;
	private String idChatTelegram;
	private double saldo = 100000.0;
	private LocalDateTime creadoEl;
	private LocalDateTime actualizadoEl;
	private String uid;
	
	public Usuario() {
		this.creadoEl = LocalDateTime.now();
		this.actualizadoEl = LocalDateTime.now();
		this.saldo = 0.0;
	}

	public Usuario(String id) {
		this.id = id;
	}

	public Usuario(String nombreUsuario, String email, String nombre, String apellido, String contrasena,
			String idChatTelegram) {
		this.nombreUsuario = nombreUsuario;
		this.email = email;
		this.nombre = nombre;
		this.apellido = apellido;
		this.contrasena = contrasena;
		this.idChatTelegram = idChatTelegram;
		this.saldo = 0.0;
		this.creadoEl = LocalDateTime.now();
		this.actualizadoEl = LocalDateTime.now();
	}

	// Getters y Setters
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
	
	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getIdChatTelegram() {
		return idChatTelegram;
	}

	public void setIdChatTelegram(String idChatTelegram) {
		this.idChatTelegram = idChatTelegram;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public LocalDateTime getCreadoEl() {
		return creadoEl;
	}

	public void setCreadoEl(LocalDateTime creadoEl) {
		this.creadoEl = creadoEl;
	}

	public LocalDateTime getActualizadoEl() {
		return actualizadoEl;
	}

	public void setActualizadoEl(LocalDateTime actualizadoEl) {
		this.actualizadoEl = actualizadoEl;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol));
	}

	@Override
	public String getPassword() {
		return contrasena;
	}

	@Override
	public String getUsername() {
		return nombreUsuario;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // Puedes implementar lógica específica si lo deseas
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // Puedes implementar lógica específica si lo deseas
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // Puedes implementar lógica específica si lo deseas
	}

	@Override
	public boolean isEnabled() {
		return true; // Puedes implementar lógica específica si lo deseas
	}

	@Override
	public String toString() {
		return "Usuario{" + "id='" + id + '\'' + ", nombreUsuario='" + nombreUsuario + '\'' + ", email='" + email + '\''
				+ ", nombre='" + nombre + '\'' + ", apellido='" + apellido + '\'' + ", idChatTelegram='"
				+ idChatTelegram + '\'' + ", saldo=" + saldo + ", creadoEl=" + creadoEl + ", actualizadoEl="
				+ actualizadoEl + '}';
	}
}

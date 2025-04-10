package com.yolo.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.yolo.backend.security.FirebaseTokenFilter;

@Configuration
public class SecurityConfig {

	@Autowired
	private FirebaseTokenFilter firebaseTokenFilter;

	@Bean
	public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);
		return authProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable())
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
					    .requestMatchers("/api/admin/**").hasRole("ADMIN")
					    .requestMatchers(
					    	    "/", 
					    	    "/index.html",
					    	    "/favicon.ico", 
					    	    "/js/**", "/css/**", "/images/**",

					    	    // 🔓 Endpoints públicos para autenticación y frontend
					    	    "/auth/**",
					    	    "/firebase-login.html",
					    	    "/firebase-register.html",
					    	    "/recover-password.html",
					    	    "/reset-password.html",

					    	    // ✅ Usuarios
					    	    "/api/usuarios",
					    	    "/api/usuarios/me",         // para obtener el perfil actual
					    	    "/api/recover",             // simulación recuperación contraseña
					    	    "/api/reset",               // cambio de contraseña simulada

					    	    // ✅ Criptomonedas (CoinGecko y gestión general)
					    	    "/cryptos/**",              // todos los GET de CoinGecko están sin prefijo /api
					    	    "/api/cryptos/**",
					    	    "/precio/**",
					    	    "/info/**",

					    	    // ✅ Transacciones
					    	    "/api/transacciones/**",

					    	    // ✅ Portafolio
					    	    "/api/portafolio/**",

					    	    // ✅ Alertas
					    	    "/alertas",

					    	    // ✅ Órdenes
					    	    "/ordenes/**",

					    	    // ✅ Notificaciones
					    	    "/notifications"
					    	).permitAll()
					    .anyRequest().authenticated()
					)

				.addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}

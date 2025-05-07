package com.yolo.backend.config;

import java.util.List;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import static org.springframework.security.config.Customizer.withDefaults;

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
	    return http
	            .cors(withDefaults()) // Habilita CORS
	            .csrf(csrf -> csrf.disable()) // ❌ Desactiva CSRF
	            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers("/api/admin/**").hasRole("ADMIN")
	                .requestMatchers(
	                	"/auth/me/details",
	                    "/", 
	                    "/index.html",
	                    "/favicon.ico", 
	                    "/js/**", "/css/**", "/images/**",

	                    // 🔓 Endpoints públicos
	                    "/auth/**",
	                    "/firebase-login.html",
	                    "/perfil.html",
	                    "/firebase-register.html",
	                    "/recover-password.html",
	                    "/reset-password.html",

	                    // ✅ APIs públicas
	                    "/api/usuarios",
	                    "/api/usuarios/me",
	                    "/api/recover",
	                    "/api/reset",
	                    "/cryptos/**",
	                    "/api/cryptos/**",
	                    "/precio/**",
	                    "/info/**",
	                    "/api/transacciones/**",
	                    "/api/portafolio/**",
	                    "/alertas",
	                    "/ordenes/**",
	                    "/notifications"
	                ).permitAll()
	                .anyRequest().authenticated()
	            )
	            .addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class)
	            .build();
	}


	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
	    CorsConfiguration configuration = new CorsConfiguration();
	    configuration.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:8080"));
	    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	    configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
	    configuration.setAllowCredentials(true);
	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    source.registerCorsConfiguration("/**", configuration);
	    return source;
	}

}

package com.tracker.backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tracker.backend.security.FirebaseTokenFilter;

import static org.springframework.security.config.Customizer.withDefaults;


import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
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
		return http.cors(withDefaults()) // Habilita CORS
				.csrf(csrf -> csrf.disable()) // Desactiva CSRF
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.requestMatchers("/auth/me/details", "/", "/index.html", "/favicon.ico", "/js/**", "/css/**",
								"/images/**",

								// Endpoints públicos
								"/auth/**", "/firebase-login.html", "/perfil.html", "/firebase-register.html",
								"/recover-password.html", "/reset-password.html",

								// APIs públicas
								"/api/usuarios", "/api/usuarios/me", "/api/recover", "/api/reset", "/cryptos/**",
								"/api/cryptos/**", "/precio/**", "/info/**", "/api/transacciones/**", "/alertas",
								"/ordenes/**", "/notifications")
						.permitAll().anyRequest().authenticated())
				.exceptionHandling(e -> e.authenticationEntryPoint(restAuthEntryPoint()))
				.addFilterBefore(firebaseTokenFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
	  CorsConfiguration cfg = new CorsConfiguration();
	  cfg.setAllowedOrigins(List.of("http://localhost:4200")); // tu front
	  cfg.setAllowedMethods(List.of("GET","POST", "PUT", "DELETE", "OPTIONS"));
	  cfg.setAllowedHeaders(List.of("Authorization","Content-Type"));
	  cfg.setExposedHeaders(List.of("Authorization"));
	  UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
	  src.registerCorsConfiguration("/**", cfg);
	  return src;
	}


	protected void configure(HttpSecurity http) throws Exception {
		http.cors() // habilita la CORS configurada arriba
				.and().csrf().disable().authorizeRequests().requestMatchers("/api/portafolios/me/**").authenticated()
				.anyRequest().permitAll().and().oauth2ResourceServer().jwt(); // o el provider que uses
	}

	@Bean
	public AuthenticationEntryPoint restAuthEntryPoint() {
		return (req, res, ex) -> {
			res.setContentType("application/json");
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			res.getWriter().write("{\"estado\":\"error\",\"mensaje\":\"No autorizado\"}");
		};
	}
	
	

}

package com.tracker.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

/*
 * Filtro para verificar el token de Firebase en las peticiones HTTP.
 */
@Component
public class FirebaseTokenFilter extends OncePerRequestFilter {

    @Autowired
    private FirebaseService firebaseService;

    /*
     * Método que se ejecuta para filtrar las peticiones HTTP.
     * Verifica el token de Firebase en la cabecera Authorization.
     * Si el token es válido, establece la autenticación en el contexto de seguridad.
     * Si el token es inválido, devuelve un error 401 (Unauthorized).
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        System.out.println("[FirebaseTokenFilter] Interceptando petición a: " + path);

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String idToken = header.substring(7);
        try {
            FirebaseToken decodedToken = firebaseService.verifyToken(idToken);
            String uid = decodedToken.getUid();

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    uid,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));
            System.out.println("UID autenticado: " + uid);

            System.out.println("Token decodificado correctamente: UID=" + uid);
            System.out.println("Contexto actual: " + SecurityContextHolder.getContext().getAuthentication());

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
            System.out.println("Contexto actual: " + SecurityContextHolder.getContext().getAuthentication());

            System.out.println("UID autenticado: " + uid);
            System.out.println("Token decodificado correctamente: UID=" + uid);
            System.out.println("Contexto actual: " + SecurityContextHolder.getContext().getAuthentication());

        } catch (FirebaseAuthException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("Token inválido en FirebaseTokenFilter: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
            return;
        }

        filterChain.doFilter(request, response);
    }
}

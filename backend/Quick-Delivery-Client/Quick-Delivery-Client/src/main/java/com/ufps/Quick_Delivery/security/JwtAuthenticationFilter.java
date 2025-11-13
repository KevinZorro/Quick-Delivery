package com.ufps.Quick_Delivery.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Si no hay header o no empieza con "Bearer ", continuar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extraer el token
            final String jwt = authHeader.substring(7);

            // ⭐ Validar el token
            if (jwtService.isTokenValid(jwt)) {
                // ⭐ Extraer información del token
                String userEmail = jwtService.extractUsername(jwt);
                String rol = jwtService.getRolFromToken(jwt);
                UUID userId = jwtService.getUserIdFromToken(jwt);

                // Log para debugging
                System.out.println("🔐 Token validado:");
                System.out.println("   📧 Email: " + userEmail);
                System.out.println("   🎭 Rol: " + rol);
                System.out.println("   🆔 User ID: " + userId);

                // Si no hay autenticación previa, crear una nueva
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userEmail,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } else {
                System.err.println("❌ Token inválido o expirado");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al validar token: " + e.getMessage());
            // Continuar sin autenticación
        }

        filterChain.doFilter(request, response);
    }
}

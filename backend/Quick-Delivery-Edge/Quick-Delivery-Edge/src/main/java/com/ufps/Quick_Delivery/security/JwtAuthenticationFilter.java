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
        
        String path = request.getRequestURI();
        
        // ⭐ PASO 1: Excluir rutas públicas (login y register)
        if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) {
            System.out.println("🔓 Ruta pública detectada: " + path + " - saltando validación JWT");
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Si no hay header o no empieza con "Bearer ", continuar sin autenticación
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⚠️ No hay token Bearer para: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extraer el token
            final String jwt = authHeader.substring(7);

            // Validar el token
            if (jwtService.isTokenValid(jwt)) {
                String userEmail = jwtService.extractUsername(jwt);
                String rol = jwtService.getRolFromToken(jwt);

                System.out.println("🔐 Token validado para: " + userEmail + " | Rol: " + rol);

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
                System.err.println("❌ Token inválido o expirado para: " + path);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al validar token: " + e.getMessage());
            e.printStackTrace();
        }

        filterChain.doFilter(request, response);
    }
}

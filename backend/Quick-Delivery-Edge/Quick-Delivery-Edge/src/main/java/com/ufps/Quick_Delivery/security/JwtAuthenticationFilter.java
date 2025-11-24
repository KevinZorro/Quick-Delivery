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
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // ⭐ PASO 1: Permitir peticiones OPTIONS (preflight CORS) sin validación
        if ("OPTIONS".equalsIgnoreCase(method)) {
            System.out.println("🔓 Petición OPTIONS (preflight) detectada: " + path + " - saltando validación JWT");
            filterChain.doFilter(request, response);
            return;
        }
        
        // ⭐ PASO 2: Excluir rutas públicas (login y register)
        if (path.startsWith("/api/auth/")) {
            System.out.println("🔓 Ruta pública detectada: " + path + " - saltando validación JWT");
            filterChain.doFilter(request, response);
         return;
        }

        
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Si no hay header o no empieza con "Bearer ", continuar sin autenticación
        if (authHeader == null || !authHeader.startsWith("Bearer ") || authHeader.equals("Bearer null")) {
        System.out.println("⚠️ No hay token Bearer válido para: " + path);
            filterChain.doFilter(request, response);
        return;
        }


        try {
            // Extraer el token
            final String jwt = authHeader.substring(7);

            // Validar el token
            if (jwtService.isTokenValid(jwt)) {
                // ⭐ CAMBIO IMPORTANTE: Extraer userId en vez de email
                UUID userId = jwtService.getUserIdFromToken(jwt);
                String userEmail = jwtService.extractUsername(jwt);
                String rol = jwtService.getRolFromToken(jwt);

                System.out.println("🔐 Token validado");
                System.out.println("   📧 Email: " + userEmail);
                System.out.println("   🆔 UserId: " + userId);
                System.out.println("   🎭 Rol: " + rol);

                // Si no hay autenticación previa, crear una nueva
                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    // ⭐ IMPORTANTE: Usar userId.toString() como principal
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userId.toString(),  // ⭐ CAMBIAR AQUÍ
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + rol))
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    System.out.println("✅ Autenticación establecida para userId: " + userId);
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

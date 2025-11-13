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
import com.ufps.Quick_Delivery.service.JwtService;

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
        
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("🌐 REQUEST: " + request.getMethod() + " " + request.getRequestURI());
        
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        System.out.println("🔍 Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ No hay token Bearer - continuando sin autenticación");
            System.out.println("═══════════════════════════════════════\n");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            System.out.println("📄 Token (primeros 50 chars): " + jwt.substring(0, Math.min(50, jwt.length())) + "...");
            
            // ⭐ Extraer userId y rol del token
            final String userId = jwtService.extractUserId(jwt);
            final String role = jwtService.extractRole(jwt);
            
            System.out.println("🆔 UserId extraído: " + userId);
            System.out.println("🎭 Rol extraído: " + role);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("🔐 Validando token...");
                
                if (jwtService.isTokenValid(jwt)) {
                    System.out.println("✅ Token válido");
                    
                    // ⭐ Crear la autoridad con el rol del token
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    System.out.println("🛡️ Autoridad creada: " + authority.getAuthority());
                    
                    // ⭐ Usar userId como principal
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    List.of(authority)
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    System.out.println("✅ Autenticación establecida exitosamente");
                    System.out.println("👤 Principal: " + authToken.getPrincipal());
                    System.out.println("🛡️ Authorities: " + authToken.getAuthorities());
                } else {
                    System.out.println("❌ Token inválido o expirado");
                }
            } else {
                if (userId == null) {
                    System.out.println("❌ No se pudo extraer userId del token");
                }
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    System.out.println("⚠️ Ya existe una autenticación en el contexto");
                }
            }
        } catch (Exception e) {
            System.out.println("💥 Error al procesar token: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("═══════════════════════════════════════\n");
        filterChain.doFilter(request, response);
    }
}

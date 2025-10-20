package com.ufps.Quick_Delivery.repartidor.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull; 

import com.ufps.Quick_Delivery.repartidor.service.RepartidorDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro JWT que se ejecuta una vez por cada solicitud.
 * Extrae el token del encabezado, lo valida y establece la autenticación
 * en el contexto de seguridad de Spring.
 * @author Ranita_Dardo_Dorada
 */

public class AuthTokenFilter extends OncePerRequestFilter {
    
    // AÑADIDO: Declaración del Logger
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);
    
    @Autowired private JwtUtils jwtUtils;
    @Autowired private RepartidorDetailsService repartidorDetailsService;

    /**
     * Procesa cada solicitud HTTP para verificar la presencia y validez de un token JWT.
     * Si el token es válido, autentica al usuario.
     * @param request La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @param filterChain La cadena de filtros.
     * @throws ServletException Si ocurre un error de servlet.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String email = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = repartidorDetailsService.loadUserByUsername(email);
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // Captura cualquier error de procesamiento y lo registra
            logger.error("No se pudo establecer la autenticación del usuario: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del encabezado 'Authorization'.
     * @param request La solicitud HTTP.
     * @return El token JWT sin el prefijo "Bearer ".
     */

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
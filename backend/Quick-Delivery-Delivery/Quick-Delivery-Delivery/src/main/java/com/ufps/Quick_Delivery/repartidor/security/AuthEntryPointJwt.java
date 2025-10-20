package com.ufps.Quick_Delivery.repartidor.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Punto de entrada de autenticación personalizado para API REST.
 * Se invoca cuando un usuario intenta acceder a un recurso seguro sin autenticación
 * o con un token inválido, devolviendo una respuesta HTTP 401 (Unauthorized) en formato JSON.
 * @author Ranita_Dardo_Dorada
 */

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    /**
     * Método invocado para enviar la respuesta HTTP 401.
     * @param request La solicitud HTTP.
     * @param response La respuesta HTTP.
     * @param authException La excepción de autenticación que ocurrió.
     * @throws IOException Si ocurre un error de entrada/salida.
     * @throws ServletException Si ocurre un error de servlet.
     */
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        
        logger.error("Error de autenticación: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", "Acceso denegado: Se requiere un token JWT válido.");
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
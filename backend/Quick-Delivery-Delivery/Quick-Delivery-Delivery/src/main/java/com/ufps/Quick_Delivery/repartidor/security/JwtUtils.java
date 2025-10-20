package com.ufps.Quick_Delivery.repartidor.security;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Clase de utilidad para la generación, extracción y validación de tokens JWT.
 * @author Ranita_Dardo_Dorada
 */

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // **IMPORTANTE**: CAMBIA esta clave a una cadena larga y secreta
    private final String jwtSecret = "TuClaveSuperSecretaDe32CaracteresOMasParaFirmarJWTs"; 
    private final int jwtExpirationMs = 86400000; // 24 horas

    /**
     * Genera un token JWT a partir de un objeto de autenticación de Spring Security.
     * Utiliza el email del usuario como sujeto (subject) del token.
     * @param authentication El objeto de autenticación actual.
     * @return El token JWT generado (String).
     */

    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Obtiene el nombre de usuario (email) a partir de un token JWT.
     * @param token El token JWT.
     * @return El nombre de usuario (email) contenido en el subject del token.
     */

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
    
    /**
     * Valida la integridad y la expiración de un token JWT.
     * @param authToken El token a validar.
     * @return true si el token es válido, false en caso contrario.
     */
    
    public boolean validateJwtToken(String authToken) {
        try {
        Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
        return true;
        // CAMBIO AQUÍ: Usamos SecurityException que es la clase superior recomendada
        } catch (io.jsonwebtoken.security.SecurityException e) { 
            logger.error("Firma JWT inválida: {}", e.getMessage());
        } catch (MalformedJwtException e) { 
            logger.error("Token JWT mal formado: {}", e.getMessage());
        } catch (ExpiredJwtException e) { 
            logger.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) { 
            logger.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) { 
            logger.error("Cadena de claims JWT vacía: {}", e.getMessage());
        }
        return false;
    }
}
package com.ufps.Quick_Delivery.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final String secretKey = "algunasecretasecretamuylargaysegura1234567890123456";

    // ⭐ Extraer el correo (subject)
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ⭐ NUEVO: Extraer el userId del claim personalizado
    public String extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", String.class);
    }

    // ⭐ NUEVO: Extraer el rol
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("rol", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // ⭐ Método helper para extraer todos los claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

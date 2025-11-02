package com.ufps.Quick_Delivery.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.UUID;

@Service
public class JwtService {

    // ⚠️ DEBE SER LA MISMA SECRET QUE EN EL MICROSERVICIO EDGE
    private final String secret = "algunasecretasecretamuylargaysegura1234567890123456";

    // ⭐ Extraer UUID del usuario desde el token
    public UUID getUserIdFromToken(String token) {
        Claims claims = extractAllClaims(token);
        String userIdStr = claims.get("userId", String.class);
        return UUID.fromString(userIdStr);
    }

    // ⭐ Extraer rol del token
    public String getRolFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("rol", String.class);
    }

    // ⭐ Extraer correo/username del token (usado por el filtro)
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ⭐ Obtener el correo del token (alias del método anterior)
    public String getCorreoFromToken(String token) {
        return extractUsername(token);
    }

    // ⭐ Validar si el token es válido (usado por el filtro)
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ⭐ Validar token (alias del método anterior)
    public boolean validateToken(String token) {
        return isTokenValid(token);
    }

    // ⭐ Extraer todos los claims del token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ⭐ Obtener la clave de firma
    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}

package com.ufps.Quick_Delivery.security;

import com.ufps.Quick_Delivery.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private final String secret = "algunasecretasecretamuylargaysegura1234567890123456"; // min 32 chars
    private final Long JWT_EXPIRATION = 1000 * 60 * 60 * 24 * 60L; // 60 Dias

    // ⭐ MÉTODO ACTUALIZADO: Ahora incluye el UUID del usuario
    public String generateToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", usuario.getId().toString()); // ⭐ AGREGAR UUID
        claims.put("rol", usuario.getRol().name());
        claims.put("sub", usuario.getCorreo());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getCorreo())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ⭐ NUEVO MÉTODO: Extraer UUID del token
    public UUID getUserIdFromToken(String token) {
        Claims claims = extractAllClaims(token);
        String userIdStr = claims.get("userId", String.class);
        return UUID.fromString(userIdStr);
    }

    // ⭐ NUEVO MÉTODO: Extraer rol del token
    public String getRolFromToken(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("rol", String.class);
    }

    // ⭐ NUEVO MÉTODO: Extraer username/email (usado por el filtro)
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ⭐ NUEVO MÉTODO: Validar si el token es válido (usado por el filtro)
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.err.println("⚠️ Token inválido: " + e.getMessage());
            return false;
        }
    }

    public String getCorreoFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        return isTokenValid(token); // Alias del método anterior
    }

    // ⭐ NUEVO MÉTODO: Extraer todos los claims del token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}

package com.ufps.Quick_Delivery.services;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final String secretKey = "algunasecretasecretamuylargaysegura1234567890123456";

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(secretKey.getBytes())
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
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
}

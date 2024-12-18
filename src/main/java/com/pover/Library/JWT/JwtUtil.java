package com.pover.Library.JWT;

import com.pover.Library.model.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Hanterar JWT-operationer som att skapa, validera, extrahera data från tokens
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long jwtExpirationMs;

    // hämtar nyckeln och utgångstiden
    // kontrollerar att nyckeln är tillräckligt lång
    public JwtUtil(@Value("${security.jwt.secret-key}") String secret,
                   @Value("${security.jwt.expiration-time}") long jwtExpirationMs) {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("Secret key must be at least 256 bits (32 characters) long.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationMs = jwtExpirationMs;
    }

    // skapar en JWT
    public String generateToken(Long id, Role role, String username, String memberNumber) {
        Map<String, Object> claims = new HashMap<>();
        //claims.put("id", id);
        claims.put("role", role.name());

        String subject = username != null ? username : memberNumber;

        if (subject == null) {
            throw new IllegalArgumentException("Either username or member number must be provided.");
        }

        // signerar och bygger token med HS256
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // läser all info från en token
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // är token giltig? tillhör token rätt användare?
    public boolean isTokenValid(String token, String username) {
        try {
            final String tokenUsername = extractAllClaims(token).getSubject();
            return username.equals(tokenUsername) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public Role extractRole(String token) {
        int roleOrdinal = extractAllClaims(token).get("role", Integer.class);
        return Role.values()[roleOrdinal];
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }


}

package com.pover.Library.JWT;

import com.pover.Library.model.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Hanterar JWT-operationer som att skapa, validera, extrahera data från tokens
@Component
public class JwtUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);


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
        claims.put("id", id);
        claims.put("role", role.name());
        if (username != null) {
            claims.put("username", username);
        }
        if (memberNumber != null) {
            claims.put("member_number", memberNumber);
        }

        // signerar och bygger token med HS256
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username != null ? username : memberNumber)
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
//    public boolean isTokenValid(String token, String username) {
//        try {
//            final String tokenUsername = extractAllClaims(token).getSubject();
//            log.info("Extracted username: {}", tokenUsername);
//            return username.equals(tokenUsername) && !isTokenExpired(token);
//        } catch (Exception e) {
//            log.error("Token validation failed: {}", e.getMessage());
//            return false;
//        }
//    }

    public boolean validateToken(String token, String username, String memberNumber) {
        final String tokenUsername = extractUsername(token);
        final String tokenMemberNumber = extractMemberNumber(token);

        boolean isUsernameValid = (tokenUsername != null && tokenUsername.equals(username));
        boolean isMemberNumberValid = (tokenMemberNumber != null && tokenMemberNumber.equals(memberNumber));
        log.info("Extracted username: {}", tokenUsername);
        log.info("Extracted mamber_number: {}", tokenMemberNumber);
        return (isUsernameValid || isMemberNumberValid) && !isTokenExpired(token);
    }



    public String extractMemberNumber(String token) {
        return extractAllClaims(token).get("member_number", String.class);
    }

    public Role extractRole(String token) {
        String roleName = extractAllClaims(token).get("role", String.class);
        return Role.valueOf(roleName);
    }

    public String extractUsername(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage());
            return null;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }


}

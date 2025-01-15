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

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long jwtExpirationMs;

    public JwtUtil(@Value("${security.jwt.secret-key}") String secret,
                   @Value("${security.jwt.expiration-time}") long jwtExpirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationMs = jwtExpirationMs;
    }


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

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username != null ? username : memberNumber)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public String extractUsername(String token) {
        return extractAllClaims(token).get("username", String.class);
    }



    public String extractMemberNumber(String token) {
        return extractAllClaims(token).get("member_number", String.class);
    }


    public Role extractRole(String token) {
        String roleString = extractAllClaims(token).get("role", String.class);
        return Role.valueOf(roleString);
    }


    public boolean validateToken(String token, String username, String memberNumber) {
        final String tokenUsername = extractUsername(token);
        final String tokenMemberNumber = extractMemberNumber(token);

        boolean isUsernameValid = (tokenUsername != null && tokenUsername.equals(username));
        boolean isMemberNumberValid = (tokenMemberNumber != null && tokenMemberNumber.equals(memberNumber));

        return (isUsernameValid || isMemberNumberValid) && !isTokenExpired(token);
    }


    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration != null && expiration.before(new Date());
    }
}

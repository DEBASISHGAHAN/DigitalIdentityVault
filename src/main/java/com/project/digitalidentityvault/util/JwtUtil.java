package com.project.digitalidentityvault.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    // Generate JWT Token
    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract All Claims (Complete Payload)
    public Map<String, Object> extractAllClaims(String token){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("Error parsing JWT Token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT Token");
        }
    }

    // Extract Email (Subject)
    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // Validate JWT Token
    public boolean isTokenValid(String token, String email){
        try {
            final String extractedEmail = extractEmail(token);
            return (extractedEmail != null && extractedEmail.equals(email) && !isTokenExpired(token));
        } catch (Exception e) {
            log.error("Error validating JWT Token: {}", e.getMessage());
            return false;
        }
    }

    // Extract Specific Claim
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    // Check if JWT is Expired
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    // Extract Expiration Date
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    // Get JWT Signing Key
    private Key getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

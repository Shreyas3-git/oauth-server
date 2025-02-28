package com.demo.oauth.service;

import com.demo.oauth.dto.TokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class TokenService {

//    // In-memory token store: token -> expiration time
//    private final Map<String, Instant> tokenStore = new ConcurrentHashMap<>();
//
//
//    // Generate a new token with 1-hour expiration
//    public TokenResponse generateToken() {
//        String accessToken = UUID.randomUUID().toString(); // Unique token
//        Instant expirationTime = Instant.now().plusSeconds(3600); // 1 hour from now
//        tokenStore.put(accessToken, expirationTime);
//        return new TokenResponse(accessToken, "Bearer", expirationTime.getEpochSecond());
//    }
//
//    // Validate a token
//    public boolean isTokenValid(String token) {
//        Instant expirationTime = tokenStore.get(token);
//        if (expirationTime == null) {
//            return false; // Token not found
//        }
//        if (Instant.now().isAfter(expirationTime)) {
//            tokenStore.remove(token); // Remove expired token
//            return false; // Token expired
//        }
//        return true; // Token is valid
//    }
//
//    @Scheduled(fixedRate = 60000) // Run every minute (adjust as needed)
//    public void removeExpiredTokens() {
//        Instant now = Instant.now();
//        tokenStore.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
//    }





    @Value("${security.jwt.secret}")
    private String jwtSecret;

    // Generate JWT token
    public TokenResponse generateToken() {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Instant expiration = Instant.now().plusSeconds(3600);

        String accessToken = Jwts.builder()
                .setSubject("user")
                .setIssuer("http://localhost:9091/oauth/token")
                .setExpiration(Date.from(expiration))
                .signWith(key)
                .compact();

        return new TokenResponse(accessToken, "Bearer", expiration.getEpochSecond());
    }

    // Validate JWT token
    public boolean isTokenValid(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

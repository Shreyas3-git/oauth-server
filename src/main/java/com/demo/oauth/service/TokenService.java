package com.demo.oauth.service;

import com.demo.oauth.dto.TokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenService {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    // Generate JWT token
    public TokenResponse generateToken() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA512");
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

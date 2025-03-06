package com.demo.oauth.service;

import com.demo.oauth.dto.TokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Arrays;
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
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return new TokenResponse(accessToken, "Bearer", expiration.getEpochSecond());
    }

    // Validate JWT token
    public boolean isTokenValid(String token) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(jwtSecret.trim());
            System.out.println("DECODED BYTES => "+ Arrays.toString(keyBytes));
            SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA512");
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        String jwtSecret = "rJD/u6re8YIcEN8hAhQL/zSAuJKb8QYchhr7DOd7NQ0zfoaEBc5kHTFTTV5YB7b3lD9PBLc18c8ctxVnaSb43Q==";

        // Decode the Base64-encoded secret into bytes
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA512");

        // Replace with your actual JWT token (e.g., from a request or debugger)
        Instant expiration = Instant.now().plusSeconds(3600);
        String accessToken = Jwts.builder()
                .setSubject("user")
                .setIssuer("http://localhost:9091/oauth/token")
                .setExpiration(Date.from(expiration))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        System.out.println("Token is valid. Claims: " + new TokenService().isTokenValid(accessToken));
    }
}

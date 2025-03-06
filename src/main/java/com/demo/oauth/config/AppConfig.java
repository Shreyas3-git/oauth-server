package com.demo.oauth.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig
{
    @Value("${security.jwt.secret}")
    private String jwtSecret;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @PostConstruct
    public void logJWTScrets() {
        log.info("JWT Base64-encoded secret Key -> {}  ",jwtSecret);
    }
}

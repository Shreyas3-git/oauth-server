package com.demo.oauth.config;

import com.demo.oauth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private TokenService tokenService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/oauth/token").permitAll() // Allow unauthenticated access to token endpoint
                        .requestMatchers("/hello").authenticated()
                        .anyRequest().authenticated()      // All other endpoints require authentication
                )
                .addFilterBefore(bearerTokenFilter(), AbstractPreAuthenticatedProcessingFilter.class);
        return http.build();
    }

    @Bean
    public AbstractPreAuthenticatedProcessingFilter bearerTokenFilter() {
        RequestHeaderAuthenticationFilter filter = new RequestHeaderAuthenticationFilter();
        filter.setPrincipalRequestHeader("Authorization"); // Expect token in Authorization header
        filter.setAuthenticationManager(authentication -> {
            String token = (String) authentication.getPrincipal();
            if (token != null && token.startsWith("Bearer ")) {
                String actualToken = token.substring(7); // Extract token after "Bearer "
                if (tokenService.isTokenValid(actualToken)) {
                    // Create a user with a default role
                    UserDetails user = new User("bearer-user", "", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
                    return new PreAuthenticatedAuthenticationToken(user, null, user.getAuthorities());
                }
            }
            throw new UsernameNotFoundException("Invalid or missing bearer token");
        });
        return filter;
    }
}
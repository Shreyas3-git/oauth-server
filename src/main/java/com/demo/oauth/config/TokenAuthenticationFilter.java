package com.demo.oauth.config;

import com.demo.oauth.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public TokenAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher("/oauth/token");
            if(antPathRequestMatcher.matches(request)) {
                filterChain.doFilter(request,response);
                return;
            }
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                sendErrorResponse(request,response,HttpStatus.UNAUTHORIZED,"Missing bearer-token in Authorization header");
                return;
            }
            String token = authHeader.substring(7);

            if (!tokenService.isTokenValid(token)) {
                logger.info("INVALID TOKEN FOUND");
                sendErrorResponse(request,response,HttpStatus.UNAUTHORIZED,"Invalid bearer token");
                return;
            } else {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken("user", null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request,response);
            }
        }
//        catch (ResponseStatusException ex) { // Catch the exception
//            sendErrorResponse(request,response,HttpStatus.INTERNAL_SERVER_ERROR,ex.getMessage());
//        }
        catch (Exception ex) {
            logger.info(String.format("Exception Message : %s",ex.getLocalizedMessage()));
            sendErrorResponse(request,response,HttpStatus.UNAUTHORIZED,ex.getMessage());
        }
    }

    public void sendErrorResponse(HttpServletRequest request,HttpServletResponse response
            ,HttpStatus status,String message) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json");

        Map<String, String> errorDetails = new LinkedHashMap<>();
        String token =   request.getHeader("Authorization");
        if(token != null ) {
            String bearerToken =  token.substring(7);
            errorDetails.put("bearer_token",bearerToken);
        }
        errorDetails.put("message", message);
        errorDetails.put("status", status.name());
        errorDetails.put("errorCode", String.valueOf(status.value()));
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(errorDetails);
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
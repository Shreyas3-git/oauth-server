package com.demo.oauth.controller;

import com.demo.oauth.dto.TokenResponse;
import com.demo.oauth.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/oauth")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @GetMapping("/token")
    public TokenResponse getToken() {
        return tokenService.generateToken();
    }
}
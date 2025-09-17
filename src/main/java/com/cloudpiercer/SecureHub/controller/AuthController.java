package com.cloudpiercer.SecureHub.controller;


import com.cloudpiercer.SecureHub.service.JwtService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwt;

    public AuthController(AuthenticationManager authManager, JwtService jwt) {
        this.authManager = authManager;
        this.jwt = jwt;
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> login(@RequestBody LoginReq req) {
        Authentication result = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        // IMPORTANT: your JwtService should be the same one used by JwtAuthFilter
        String token = jwt.issue(result.getName(), result.getAuthorities());
        return Map.of("accessToken", token);
    }

    public record LoginReq(@NotBlank String username, @NotBlank String password) {}
}
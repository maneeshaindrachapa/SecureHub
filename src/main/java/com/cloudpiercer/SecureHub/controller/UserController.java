package com.cloudpiercer.SecureHub.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        return Map.of("user", auth.getName(), "authorities", auth.getAuthorities());
    }
}

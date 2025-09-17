package com.cloudpiercer.SecureHub.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}

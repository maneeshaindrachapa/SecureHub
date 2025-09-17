package com.cloudpiercer.SecureHub.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cors-demo")
@CrossOrigin(origins = { "http://localhost:3000" })
public class CorsDemoController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello CORS!";
    }
}

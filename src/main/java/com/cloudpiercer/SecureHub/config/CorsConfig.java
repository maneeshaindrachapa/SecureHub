package com.cloudpiercer.SecureHub.config;

import org.springframework.context.annotation.*;
import org.springframework.web.cors.*;
import java.util.List;

/**
 * Centralized CORS configuration used by Spring Security's .cors():
 * - Allows local React dev server origin
 * - Permits common HTTP methods and all headers
 * - Allows credentials when needed (cookies/authorization headers)
 */
@Configuration
@Primary
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        // Frontend origin(s) allowed to call this API
        c.setAllowedOrigins(List.of("http://localhost:3000"));
        // Allowed HTTP methods for cross-origin requests
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow any header (Authorization, Content-Type, etc.)
        c.setAllowedHeaders(List.of("*"));
        // Send CORS response with Allow-Credentials: true
        c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }
}

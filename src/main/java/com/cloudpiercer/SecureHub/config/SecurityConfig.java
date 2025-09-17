package com.cloudpiercer.SecureHub.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.cloudpiercer.SecureHub.filter.JwtAuthFilter;

@Configuration
@EnableMethodSecurity // enable @PreAuthorize / method-level authorization
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthFilter jwt,
            @Qualifier("corsConfigurationSource") CorsConfigurationSource cors) throws Exception {

        return http
                // Stateless API: disable CSRF and sessions (no login form, no server state)
                .csrf(csrf -> csrf
                        // Permit H2 console by ignoring CSRF there (console uses frames + POST forms)
                        .ignoringRequestMatchers(PathRequest.toH2Console())
                        .disable())
                // CORS is centralized via CorsConfigurationSource bean
                .cors(c -> c.configurationSource(cors))
                // No HttpSession: JWT carries identity
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Route-level authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers("/public/**", "/auth/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                // Allow same-origin frames for H2 console
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                // Consistent 401/403 JSON-less responses (status codes only)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> res.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((req, res, e) -> res.setStatus(HttpServletResponse.SC_FORBIDDEN)))
                // Validate JWT before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

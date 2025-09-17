package com.cloudpiercer.SecureHub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Authentication plumbing for form-less (API) login:
 * - Exposes a BCrypt PasswordEncoder
 * - Configures a DaoAuthenticationProvider using the app's UserDetailsService
 * - Publishes an AuthenticationManager consumed by the /auth/login endpoint
 */
@Configuration
public class AuthManagerConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider daoAuthProvider(UserDetailsService uds, PasswordEncoder enc) {
        // Provider that verifies username/password against UserDetailsService
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(enc);
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider provider) {
        // Simple manager with a single provider; used by AuthController
        return new ProviderManager(provider);
    }
}

package com.cloudpiercer.SecureHub.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for handling JWT operations.
 */
@Service
public class JwtService {

    private final String base64Secret;
    private SecretKey key;

    public JwtService(@Value("${security.jwt.secret-base64:}") String base64Secret) {
        this.base64Secret = base64Secret;
    }

    @PostConstruct
    void initKey() {
        if (base64Secret == null || base64Secret.isBlank()) {
            // Dev fallback: generate a random key so the app still starts.
            // DO NOT use this in production: tokens will be invalid after restart.
            this.key = Jwts.SIG.HS256.key().build(); // generates a secure random HS256 key
            System.err.println(
                    "[WARN] No security.jwt.secret-base64 set. Using a random dev key (tokens reset on restart).");
            return;
        }
        byte[] decoded = Decoders.BASE64.decode(base64Secret);
        if (decoded.length < 32) {
            throw new IllegalStateException(
                    "security.jwt.secret-base64 must decode to at least 32 bytes (256 bits) for HS256.");
        }
        this.key = Keys.hmacShaKeyFor(decoded);
    }

    public String issue(String username, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        String scope = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        return Jwts.builder()
                .subject(username)
                .claim("scope", scope)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Authentication toAuthentication(String token) {
        Jws<Claims> jws = parse(token);
        String user = jws.getPayload().getSubject();
        String scope = jws.getPayload().get("scope", String.class);
        List<org.springframework.security.core.authority.SimpleGrantedAuthority> auths = Arrays
                .stream(Optional.ofNullable(scope).orElse("").split(" "))
                .filter(s -> !s.isBlank())
                .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                .toList();
        return new UsernamePasswordAuthenticationToken(user, null, auths);
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }
}
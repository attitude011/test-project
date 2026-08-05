package com.example.transaction.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@RestController
public class JwtTokenGenerator {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Generates a short-lived HS256-signed JWT for local development and Postman testing.
     *
     * <p>The token is signed with the secret configured under {@code jwt.secret} in
     * {@code application.yml}, carries the subject {@code "local-user"}, and expires
     * <strong>one hour</strong> after issuance. This endpoint is intentionally left
     * unauthenticated so that a fresh token can always be obtained without a pre-existing
     * credential.
     *
     * @return a compact, URL-safe JWT string in the format
     *         {@code <header>.<payload>.<signature>}; never {@code null}
     */
    @GetMapping("/generate-token")
    public String generateToken() {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject("local-user")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(key)
                .compact();
    }
}

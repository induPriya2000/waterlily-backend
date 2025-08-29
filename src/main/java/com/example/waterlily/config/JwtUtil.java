package com.example.waterlily.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {
    private final Key key;          // secret key used to sign/verify JWTs
    private final long ttlMillis;   // token lifetime in milliseconds

    // Reads values from application.yml: app.jwt-secret and app.jwt-ttl-days
    public JwtUtil(
            @Value("${app.jwt-secret}") String secret,
            @Value("${app.jwt-ttl-days}") long ttlDays) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.ttlMillis = Duration.ofDays(ttlDays).toMillis();
    }

    // Create a signed JWT containing user id (uid) and email (subject)
    public String create(Long userId, String email) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)                               // sets "sub" claim
                .claim("uid", userId)                         // custom claim for user id
                .issuedAt(now)                                // iat
                .expiration(new Date(now.getTime() + ttlMillis)) // exp
                .signWith(key)                                // HMAC-SHA signature with our key
                .compact();                                   // serialize to String
    }

    // Parse & verify a token; returns its claims or throws if invalid/expired
    public io.jsonwebtoken.Claims parse(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)   // uses same key to verify signature
                .build()
                .parseSignedClaims(token)
                .getPayload();     // the Claims (sub, exp, uid, etc.)
    }
}

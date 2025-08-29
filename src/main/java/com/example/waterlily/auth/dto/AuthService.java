package com.example.waterlily.auth.dto;

import com.example.waterlily.config.JwtUtil;
import com.example.waterlily.persistence.User;
import com.example.waterlily.persistence.UserRepository;
import java.time.Instant;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository users;
    private final JwtUtil jwt;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository users, JwtUtil jwt) {
        this.users = users;
        this.jwt = jwt;
    }

    // Helper class to hold both token + user
    public record TokenAndUser(String token, User user) {}

    public TokenAndUser signup(String email, String name, String password) {
        // check if user already exists
        users.findByEmail(email.trim().toLowerCase())
                .ifPresent(u -> { throw new RuntimeException("Email already registered"); });

        // create new user
        User u = new User();
        u.setEmail(email.trim().toLowerCase());
        u.setName(name);
        u.setPasswordHash(encoder.encode(password));
        u.setCreatedAt(Instant.now());
        u = users.save(u);

        // create JWT token
        String token = jwt.create(u.getId(), u.getEmail());
        return new TokenAndUser(token, u);
    }

    public TokenAndUser login(String email, String password) {
        User u = users.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(password, u.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwt.create(u.getId(), u.getEmail());
        return new TokenAndUser(token, u);
    }
}

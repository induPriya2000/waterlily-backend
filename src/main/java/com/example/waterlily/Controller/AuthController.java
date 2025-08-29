package com.example.waterlily.Controller;

import com.example.waterlily.auth.dto.*;
import com.example.waterlily.config.JwtUtil;
import com.example.waterlily.persistence.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService auth;
    private final UserRepository users;
    private final JwtUtil jwt;

    public AuthController(AuthService auth, UserRepository users, JwtUtil jwt) {
        this.auth = auth;
        this.users = users;
        this.jwt = jwt;
    }

    // helper: set cookie with token
    private void setTokenCookie(HttpServletResponse res, String token) {
        Cookie c = new Cookie("token", token);
        c.setHttpOnly(true);
        c.setPath("/");
        c.setMaxAge(60 * 60 * 24 * 7); // 7 days
        c.setSecure(false);            // true in production with HTTPS
        c.setAttribute("SameSite", "Lax");
        res.addCookie(c);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req, HttpServletResponse res) {
        try {
            var t = auth.signup(req.email, req.name, req.password);
            setTokenCookie(res, t.token());
            return ResponseEntity.ok(new AuthUserDto(t.user().getId(), t.user().getEmail(), t.user().getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req, HttpServletResponse res) {
        try {
            var t = auth.login(req.email, req.password);
            setTokenCookie(res, t.token());
            return ResponseEntity.ok(new AuthUserDto(t.user().getId(), t.user().getEmail(), t.user().getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse res) {
        Cookie c = new Cookie("token", "");
        c.setHttpOnly(true);
        c.setPath("/");
        c.setMaxAge(0); // delete
        c.setSecure(false);
        c.setAttribute("SameSite", "Lax");
        res.addCookie(c);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authn) {
        if (authn == null) return ResponseEntity.ok(Map.of("user", null));
        var name = authn.getName();
        var u = users.findByEmail(name).orElse(null);
        if (u == null) return ResponseEntity.ok(Map.of("user", null));
        return ResponseEntity.ok(Map.of("user", new AuthUserDto(u.getId(), u.getEmail(), u.getName())));
    }
}

package com.example.waterlily.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            Cookie token = Arrays.stream(cookies)
                    .filter(c -> "token".equals(c.getName()))
                    .findFirst()
                    .orElse(null);

            if (token != null) {
                try {
                    var claims = jwtUtil.parse(token.getValue());     // verify signature & expiry
                    Long uid = claims.get("uid", Long.class);
                    String email = claims.getSubject();               // the “sub” we set

                    // Build a Spring Security principal (we keep it simple: role USER)
                    var principal = User.withUsername(email)
                            .password("")                                 // not used here
                            .roles("USER")
                            .build();

                    var auth = new UsernamePasswordAuthenticationToken(
                            principal, null, principal.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

                    // Mark this request as authenticated
                    SecurityContextHolder.getContext().setAuthentication(auth);

                } catch (Exception ignored) {
                    // Invalid/expired token? Ignore and continue unauthenticated.
                }
            }
        }

        chain.doFilter(req, res);
    }
}

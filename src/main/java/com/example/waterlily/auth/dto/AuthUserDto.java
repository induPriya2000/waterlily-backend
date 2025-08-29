package com.example.waterlily.auth.dto;

public class AuthUserDto {
    public Long id;
    public String email;
    public String name;
    public AuthUserDto(Long id, String email, String name) {
        this.id = id; this.email = email; this.name = name;
    }
}

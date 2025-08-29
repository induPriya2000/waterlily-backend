package com.example.waterlily.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class SignupRequest {
    @Email @NotBlank public String email;
    @NotBlank public String password;
    public String name;
}

package com.chrisnkl.cfbackend.infrastructure.dto.request.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @Email(message = "Invalid email format") String email,
        @Size(min = 8, message = "Password must be at least 8 characters") String password
) {

    public AuthRequest {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
    }
}

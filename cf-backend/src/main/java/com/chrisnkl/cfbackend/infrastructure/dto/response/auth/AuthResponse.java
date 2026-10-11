package com.chrisnkl.cfbackend.infrastructure.dto.response.auth;

public record AuthResponse(

        String accessToken,
        String refreshToken

) {

    public AuthResponse {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("Access token cannot be null or blank");
        }
    }

}

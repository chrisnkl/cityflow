package com.chrisnkl.cfbackend.infrastructure.dto.request.auth;

public record RefreshTokenRequest(

        String refreshToken

) {

    public RefreshTokenRequest {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token cannot be null or blank");
        }
    }
}

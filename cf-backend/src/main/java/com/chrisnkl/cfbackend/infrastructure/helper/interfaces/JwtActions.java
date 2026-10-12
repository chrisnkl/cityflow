package com.chrisnkl.cfbackend.infrastructure.helper.interfaces;

import com.chrisnkl.cfbackend.infrastructure.dto.response.auth.AuthResponse;
import io.jsonwebtoken.Claims;

import java.util.Set;

public interface JwtActions {

    AuthResponse generateTokens(String email, Set<String> authorities);
    AuthResponse refreshTokens(String refreshToken);
    Claims parseAccessToken(String token);
    Claims parseRefreshToken(String token);
    boolean validateAccessToken(String token);
    boolean validateRefreshToken(String token);

}

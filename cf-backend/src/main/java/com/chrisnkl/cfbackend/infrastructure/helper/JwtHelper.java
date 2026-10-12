package com.chrisnkl.cfbackend.infrastructure.helper;

import com.chrisnkl.cfbackend.infrastructure.dto.response.auth.AuthResponse;
import com.chrisnkl.cfbackend.infrastructure.helper.interfaces.JwtActions;
import com.chrisnkl.cfbackend.infrastructure.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@Slf4j
public class JwtHelper implements JwtActions {

    private final JwtProperties properties;
    private final SecretKey signingKey;

    public JwtHelper(JwtProperties properties) {
        this.properties = properties;

        if (properties.issuer() == null || properties.issuer().isBlank()) {
            throw new IllegalArgumentException("JWT issuer cannot be null or blank");
        }
        if (properties.secret() == null || properties.secret().isBlank()) {
            throw new IllegalArgumentException("JWT secret cannot be null or blank");
        }

        if (properties.accessTokenExpiration() == null
                || properties.accessTokenExpiration().isZero()
                || properties.accessTokenExpiration().isNegative()) {
            throw new IllegalArgumentException("JWT access-token expiration must be positive");
        }

        if (properties.refreshTokenExpiration() == null
                || properties.refreshTokenExpiration().isZero()
                || properties.refreshTokenExpiration().isNegative()) {
            throw new IllegalArgumentException("JWT refresh-token expiration must be positive");
        }

        try {
            byte[] keyBytes = Decoders.BASE64.decode(properties.secret());
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (DecodingException e) {
            log.error("Invalid JWT secret: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT secret", e);
        } catch (WeakKeyException e) {
            log.error("Weak JWT secret: {}", e.getMessage());
            throw new IllegalArgumentException("Weak JWT secret", e);
        } catch (Exception e) {
            log.error("Error initializing JwtHelper: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize JwtHelper", e);
        }
    }

    @Override
    public AuthResponse generateTokens(String email, Set<String> authorities) {
        Set<String> normalizedAuthorities = normalizeAuthorities(authorities);

        String accessToken = generateAccessToken(email, normalizedAuthorities);
        String refreshToken = generateRefreshToken(email, normalizedAuthorities);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public AuthResponse refreshTokens(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token cannot be null or blank");
        }

        Claims claims = parseRefreshToken(refreshToken);
        String email = claims.getSubject();
        Set<String> authorities = extractAuthorities(claims);

        return generateTokens(email, authorities);
    }

    @Override
    public Claims parseAccessToken(String token) {
        return parseToken(token, TokenType.ACCESS);
    }

    @Override
    public Claims parseRefreshToken(String token) {
        return parseToken(token, TokenType.REFRESH);
    }

    @Override
    public boolean validateAccessToken(String token) {
        return validateTokenType(token, TokenType.ACCESS);
    }

    @Override
    public boolean validateRefreshToken(String token) {
        return validateTokenType(token, TokenType.REFRESH);
    }

    private Claims parseToken(String token, TokenType tokenType) {
        if (token == null || token.isBlank()) {
            throw new JwtException("Token cannot be null or blank");
        }

        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(properties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        if (!tokenType.name().equalsIgnoreCase(claims.get("type", String.class))) {
            throw new JwtException("Invalid token type: expected " + tokenType.name() + " type.");
        }

        if (claims.getSubject() == null || claims.getSubject().isBlank()) {
            throw new JwtException("Invalid token: subject (email) is missing.");
        }

        return claims;
    }

    private String generateAccessToken(String email, Set<String> authorities) {
        return generateToken(email, authorities, TokenType.ACCESS, properties.accessTokenExpiration());
    }

    private String generateRefreshToken(String email, Set<String> authorities) {
        return generateToken(email, authorities, TokenType.REFRESH, properties.refreshTokenExpiration());
    }

    private String generateToken(String email, Set<String> authorities, TokenType tokenType, Duration expiration) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        Instant now = Instant.now();

        JwtBuilder builder = Jwts.builder()
                .subject(email)
                .claim("type", tokenType.name())
                .issuer(properties.issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)));

        if (tokenType == TokenType.ACCESS) {
            builder.claim("roles", normalizeAuthorities(authorities));
        }

        return builder
                .signWith(signingKey)
                .compact();
    }

    private boolean validateTokenType(String token, TokenType expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .requireIssuer(properties.issuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return expectedType.name().equalsIgnoreCase(claims.get("type", String.class));
        } catch (IllegalArgumentException | JwtException e) {
            return false;
        }
    }

    private Set<String> normalizeAuthorities(Set<String> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return Set.of();
        }

        Set<String> normalized = new LinkedHashSet<>();
        for (String authority : authorities) {
            if (authority != null && !authority.isBlank()) {
                normalized.add(authority);
            }
        }

        return Set.copyOf(normalized);
    }

    private Set<String> extractAuthorities(Claims claims) {
        Object rolesClaim = claims.get("roles");
        if (!(rolesClaim instanceof Iterable<?> roles)) {
            return Set.of();
        }

        Set<String> authorities = new LinkedHashSet<>();
        for (Object role : roles) {
            if (role instanceof String roleName && !roleName.isBlank()) {
                authorities.add(roleName);
            }
        }

        return Set.copyOf(authorities);
    }
}

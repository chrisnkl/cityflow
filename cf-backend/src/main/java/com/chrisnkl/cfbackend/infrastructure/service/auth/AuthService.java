package com.chrisnkl.cfbackend.infrastructure.service.auth;

import com.chrisnkl.cfbackend.infrastructure.dto.request.auth.AuthRequest;
import com.chrisnkl.cfbackend.infrastructure.dto.request.auth.RefreshTokenRequest;
import com.chrisnkl.cfbackend.infrastructure.dto.response.auth.AuthResponse;
import com.chrisnkl.cfbackend.infrastructure.helper.JwtHelper;
import com.chrisnkl.cfbackend.infrastructure.helper.RoleManagementHelper;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.UserEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtHelper jwtHelper;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JpaUserRepository userRepository;
    private final RoleManagementHelper roleManagementHelper;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest authRequest) {
        log.info("AuthService.login received login request with email: {}", authRequest.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.email(),
                        authRequest.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        assert userDetails!=null;

        Set<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return jwtHelper.generateTokens(userDetails.getUsername(), authorities);
    }

    public AuthResponse register(AuthRequest authRequest) {
        log.info("AuthService.register received a register request: {}", authRequest.email());

        if (userRepository.findByEmail(authRequest.email()).isPresent()) {
            throw new BadCredentialsException("Email is already registered");
        }

        UserEntity newUser = UserEntity.builder()
                .email(authRequest.email())
                .password(passwordEncoder.encode(authRequest.password()))
                .build();

        userRepository.save(newUser);

        return jwtHelper.generateTokens(authRequest.email(), roleManagementHelper.defaultRolesWithPermissions());
    }

    public AuthResponse refreshTokens(RefreshTokenRequest refreshTokenRequest) {
        log.info("AuthService.refreshTokens received a refresh token request");

        Claims claims;
        try {
            claims = jwtHelper.parseRefreshToken(refreshTokenRequest.refreshToken());
        } catch (JwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid refresh token", e);
        }

        String email = claims.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!userDetails.isEnabled()) throw new BadCredentialsException("User account is disabled");
        if (!userDetails.isAccountNonLocked()) throw new BadCredentialsException("User account is locked");
        if (!userDetails.isCredentialsNonExpired()) throw new BadCredentialsException("User credentials are expired");

        log.info("AuthService.refreshTokens successfully validated refresh token for user: {}", email);
        Set<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return jwtHelper.generateTokens(userDetails.getUsername(), authorities);
    }
}

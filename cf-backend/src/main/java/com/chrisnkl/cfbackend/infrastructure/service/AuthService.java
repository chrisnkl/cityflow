package com.chrisnkl.cfbackend.infrastructure.service;

import com.chrisnkl.cfbackend.domain.exception.auth.FailedRegistrationException;
import com.chrisnkl.cfbackend.domain.exception.auth.UserAlreadyExistsException;
import com.chrisnkl.cfbackend.domain.model.auth.Role;
import com.chrisnkl.cfbackend.infrastructure.dto.request.auth.AuthRequest;
import com.chrisnkl.cfbackend.infrastructure.dto.request.auth.RefreshTokenRequest;
import com.chrisnkl.cfbackend.infrastructure.dto.response.auth.AuthResponse;
import com.chrisnkl.cfbackend.infrastructure.helper.JwtHelper;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.RoleEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.entity.auth.UserEntity;
import com.chrisnkl.cfbackend.infrastructure.persistence.repository.JpaRoleRepository;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtHelper jwtHelper;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JpaUserRepository userRepository;
    private final JpaRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final static UUID ROLE_USER_ID = UUID.fromString("10000000-0000-0000-0000-000000000003");

    public AuthResponse login(AuthRequest authRequest) {
        log.info("AuthService.login received login request with email: {}", authRequest.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.email(),
                        authRequest.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (userDetails == null) throw new UsernameNotFoundException("Invalid email or password");

        Set<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return jwtHelper.generateTokens(userDetails.getUsername(), authorities);
    }

    @Transactional
    public AuthResponse register(AuthRequest authRequest) {
        log.info("AuthService.register received a register request: {}", authRequest.email());

        if (userRepository.findByEmail(authRequest.email()).isPresent()) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        RoleEntity userRole = roleRepository.findById(ROLE_USER_ID).orElseThrow(() -> new FailedRegistrationException("Failed to find default user role."));

        UserEntity newUser = UserEntity.builder()
                .email(authRequest.email())
                .password(passwordEncoder.encode(authRequest.password()))
                .roles(Set.of(userRole))
                .build();

        userRepository.save(newUser);

        UserDetails loadUser = userDetailsService.loadUserByUsername(newUser.getEmail());

        Set<String> authorities = loadUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return jwtHelper.generateTokens(loadUser.getUsername(), authorities);
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

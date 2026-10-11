package com.chrisnkl.cfbackend.infrastructure.service.auth;

import com.chrisnkl.cfbackend.infrastructure.dto.request.auth.AuthRequest;
import com.chrisnkl.cfbackend.infrastructure.dto.response.auth.AuthResponse;
import com.chrisnkl.cfbackend.infrastructure.helper.JwtHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtHelper jwtHelper;
    private final AuthenticationManager authenticationManager;

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
        log.info("AuthService.register received reegister request with email: {}", authRequest.email());
        return jwtHelper.generateTokens(authRequest.email(), null);
    }

}

package com.chrisnkl.cfbackend.infrastructure.filter;

import com.chrisnkl.cfbackend.common.header.Header;
import com.chrisnkl.cfbackend.infrastructure.helper.JwtHelper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(Header.AUTHORIZATION.getValue());
        try {
            String[] parts = header != null ? header.split(" ") : new String[0];
            if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
                String token = parts[1];

                Claims claims = jwtHelper.parseAccessToken(token);
                Object roleClaims = claims.getOrDefault("roles", new ArrayList<>());

                String email = claims.getSubject();
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                if (roleClaims instanceof List<?> roles) {
                    for (Object role : roles) {
                        if (role instanceof String roleStr) {
                            authorities.add(new SimpleGrantedAuthority(roleStr));
                        }
                    }
                }

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(context);

            }
        } catch (Exception e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}

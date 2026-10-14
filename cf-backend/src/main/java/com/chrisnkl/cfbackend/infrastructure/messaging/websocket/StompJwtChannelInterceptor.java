package com.chrisnkl.cfbackend.infrastructure.messaging.websocket;

import com.chrisnkl.cfbackend.infrastructure.helper.JwtHelper;
import io.jsonwebtoken.Claims;
import org.jspecify.annotations.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private final JwtHelper jwtHelper;

    public StompJwtChannelInterceptor(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            accessor.setUser(authenticate(accessor.getFirstNativeHeader("Authorization")));
        } else if (accessor.getUser() == null) {
            throw new IllegalArgumentException("An authenticated WebSocket connection is required.");
        }
        return message;
    }

    private UsernamePasswordAuthenticationToken authenticate(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("A bearer access token is required for WebSocket connections.");
        }

        Claims claims = jwtHelper.parseAccessToken(authorizationHeader.substring("Bearer ".length()).trim());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        Object rolesClaim = claims.get("roles");
        if (rolesClaim instanceof Iterable<?> roles) {
            for (Object role : roles) {
                if (role instanceof String authority && !authority.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority(authority));
                }
            }
        }
        return UsernamePasswordAuthenticationToken.authenticated(claims.getSubject(), null, authorities);
    }
}

package com.chrisnkl.cfbackend.infrastructure.filter.filter;

import com.chrisnkl.cfbackend.common.header.Header;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class MDCFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestId = request.getHeader(Header.REQUEST_ID.getValue());
        if (requestId == null || requestId.isBlank()) requestId = UUID.randomUUID().toString();
        MDC.put(Header.REQUEST_ID.getValue(), requestId);
        try {
            response.setHeader(Header.REQUEST_ID.getValue(), requestId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(Header.REQUEST_ID.getValue());
        }
    }
}

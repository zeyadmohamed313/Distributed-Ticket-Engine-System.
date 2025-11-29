package com.flashsale.ticketing.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {

            String requestId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put("requestId", requestId);

            // Real IP address (even behind Nginx Load Balancer)
            String clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty()) {
                clientIp = request.getRemoteAddr();
            }
            MDC.put("clientIp", clientIp);

            String method = request.getMethod();
            String uri = request.getRequestURI();
            MDC.put("apiMethod", method);
            MDC.put("apiUri", uri);

            filterChain.doFilter(request, response);

        } finally {
            MDC.clear();
        }
    }
}
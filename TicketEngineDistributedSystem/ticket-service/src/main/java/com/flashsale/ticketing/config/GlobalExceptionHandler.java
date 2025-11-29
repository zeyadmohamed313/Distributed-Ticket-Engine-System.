package com.flashsale.ticketing.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, HttpServletRequest request) {

        log.error("🔥 SYSTEM ERROR at [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred. Reference ID: " + org.slf4j.MDC.get("requestId"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleLogicException(RuntimeException ex) {
        log.warn("⚠️ Business Logic Error: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

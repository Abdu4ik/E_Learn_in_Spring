package com.tafakkoor.e_learn.services;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiterService {
    private static final double DEFAULT_REQUEST_PER_SECOND = 100.0;
    private final Map<String, RateLimiter> rateLimiters;

    public RateLimiterService() {
        this(DEFAULT_REQUEST_PER_SECOND);
    }

    public RateLimiterService(double requestPerSecond) {
        rateLimiters = new ConcurrentHashMap<>();
    }

    public boolean allowRequest(HttpServletRequest request) {
        String key = getUserKey(request);
        RateLimiter rateLimiter = rateLimiters.computeIfAbsent(key, k -> RateLimiter.create(DEFAULT_REQUEST_PER_SECOND));
        return rateLimiter.tryAcquire();
    }

    private String getUserKey(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        System.out.println("ipAddress = " + ipAddress);
        return ipAddress;
    }
}


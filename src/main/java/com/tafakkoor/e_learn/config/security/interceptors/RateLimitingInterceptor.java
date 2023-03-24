package com.tafakkoor.e_learn.config.security.interceptors;

import com.tafakkoor.e_learn.exceptions.TooManyRequestsException;
import com.tafakkoor.e_learn.services.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class RateLimitingInterceptor implements HandlerInterceptor {

    private static final RateLimiterService rateLimiter = new RateLimiterService(100.0);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!rateLimiter.allowRequest(request)) {
            throw new TooManyRequestsException("Too many requests");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}


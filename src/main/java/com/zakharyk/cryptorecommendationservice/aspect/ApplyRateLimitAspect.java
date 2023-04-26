package com.zakharyk.cryptorecommendationservice.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import com.zakharyk.cryptorecommendationservice.annotation.ApplyRateLimit;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ApplyRateLimitAspect {
    private final Cache<String, Integer> rateLimitCache;

    @Autowired
    private HttpServletRequest request;

    @Around("@annotation(applyRateLimit)")
    public Object applyRateLimit(ProceedingJoinPoint jp, ApplyRateLimit applyRateLimit) throws Throwable {
        var callsPerMinuteAllowed = applyRateLimit.callsPerMinuteAllowed();
        var ipAddress = request.getRemoteAddr();
        var requestURI = request.getRequestURI();
        var key = ipAddress + "_" + requestURI;

        var actualNumberOfUserCalls = rateLimitCache.asMap().get(key);
        if (actualNumberOfUserCalls != null && actualNumberOfUserCalls >= callsPerMinuteAllowed) {
            log.warn("user with ip={} has reached the limit to the endpoint={}", ipAddress, requestURI);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "You reached the rate limit. Try in one minute");
        }

        var result = jp.proceed();
        // increase counter
        rateLimitCache.asMap().merge(key, 1, Integer::sum);
        return result;
    }
}

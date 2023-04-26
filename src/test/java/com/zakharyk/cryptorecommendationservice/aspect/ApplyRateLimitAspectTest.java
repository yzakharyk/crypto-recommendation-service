package com.zakharyk.cryptorecommendationservice.aspect;

import com.github.benmanes.caffeine.cache.Cache;
import com.zakharyk.cryptorecommendationservice.annotation.ApplyRateLimit;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplyRateLimitAspectTest {
    @Mock
    private Cache<String, Integer> rateLimitCache;
    @InjectMocks
    private ApplyRateLimitAspect applyRateLimitAspect;
    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(applyRateLimitAspect, "request", request);
    }

    @Test
    @SuppressWarnings("unchecked")
    void applyRateLimit_WithinLimit_Success() throws Throwable {
        var callsPerMinuteAllowed = 5L;
        var ipAddress = "127.0.0.1";
        var requestURI = "/api/test";
        var cacheKey = ipAddress + "_" + requestURI;

        // Mock HttpServletRequest
        when(request.getRemoteAddr()).thenReturn(ipAddress);
        when(request.getRequestURI()).thenReturn(requestURI);

        var innerCacheMap = (ConcurrentHashMap<String,Integer>)mock(ConcurrentHashMap.class);
        when(rateLimitCache.asMap()).thenReturn(innerCacheMap);

        // Create a sample JoinPoint
        ProceedingJoinPoint jp = mock(ProceedingJoinPoint.class);
        var result = new Object();
        when(jp.proceed()).thenReturn(result);

        // Create ApplyRateLimit annotation
        ApplyRateLimit applyRateLimit = mock(ApplyRateLimit.class);
        when(applyRateLimit.callsPerMinuteAllowed()).thenReturn(callsPerMinuteAllowed);

        // Act
        var actual = applyRateLimitAspect.applyRateLimit(jp, applyRateLimit);

        // Assert
        assertEquals(result, actual);

    }

    @Test
    @SuppressWarnings("unchecked")
    void applyRateLimit_ExceedsLimit() throws Throwable {
        // Arrange
        var callsPerMinuteAllowed = 5L;
        var ipAddress = "127.0.0.1";
        var requestURI = "/api/test";
        var cacheKey = ipAddress + "_" + requestURI;

        // Mock HttpServletRequest
        when(request.getRemoteAddr()).thenReturn(ipAddress);
        when(request.getRequestURI()).thenReturn(requestURI);

        var innerCacheMap = (ConcurrentHashMap<String,Integer>)mock(ConcurrentHashMap.class);
        when(innerCacheMap.get(cacheKey)).thenReturn(10);
        when(rateLimitCache.asMap()).thenReturn(innerCacheMap);

        // Create a sample JoinPoint
        ProceedingJoinPoint jp = mock(ProceedingJoinPoint.class);

        // Create ApplyRateLimit annotation
        var applyRateLimit = mock(ApplyRateLimit.class);
        when(applyRateLimit.callsPerMinuteAllowed()).thenReturn(callsPerMinuteAllowed);

        // Act and Assert
        assertThrows(ResponseStatusException.class, () -> applyRateLimitAspect.applyRateLimit(jp, applyRateLimit));
    }
}
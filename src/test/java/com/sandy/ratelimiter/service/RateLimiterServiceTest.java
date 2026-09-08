package com.sandy.ratelimiter.service;

import com.sandy.ratelimiter.config.RateLimitProperties;
import com.sandy.ratelimiter.dto.RateLimitResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.sandy.ratelimiter.config.RateLimitProperties;
import com.sandy.ratelimiter.dto.RateLimitResponse;
import com.sandy.ratelimiter.exception.RateLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RateLimiterServiceTest {

    private RedisService redisService;
    private RateLimitProperties rateLimitProperties;
    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {

        redisService = mock(RedisService.class);
        rateLimitProperties = new RateLimitProperties();

        rateLimitProperties.setMaxRequests(5);
        rateLimitProperties.setWindowSeconds(60);

        rateLimiterService =
                new RateLimiterService(
                        redisService,
                        rateLimitProperties
                );
    }

    @Test
    void shouldRejectRequestWhenLimitIsExceeded() {

        when(redisService.checkRateLimit(
                "rate_limit:sai",
                5,
                60
        )).thenReturn(0L);

        assertThrows(
                RateLimitExceededException.class,
                () -> rateLimiterService.checkRequest("sai")
        );
    }
    @Test
    void shouldReturnCorrectRemainingRequests() {

        when(redisService.checkRateLimit(
                "rate_limit:sai",
                5,
                60
        )).thenReturn(3L);

        RateLimitResponse response =
                rateLimiterService.checkRequest("sai");

        assertTrue(response.isAllowed());
        assertEquals(2, response.getRemainingRequests());
    }
    @Test
    void shouldUseIndependentLimitsForDifferentUsers() {

        when(redisService.checkRateLimit(
                "rate_limit:sai",
                5,
                60
        )).thenReturn(5L);

        when(redisService.checkRateLimit(
                "rate_limit:alice",
                5,
                60
        )).thenReturn(1L);

        RateLimitResponse saiResponse =
                rateLimiterService.checkRequest("sai");

        RateLimitResponse aliceResponse =
                rateLimiterService.checkRequest("alice");

        assertEquals(0, saiResponse.getRemainingRequests());
        assertEquals(4, aliceResponse.getRemainingRequests());
    }
}
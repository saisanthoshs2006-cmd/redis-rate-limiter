package com.sandy.ratelimiter.service;

import com.sandy.ratelimiter.config.RateLimitProperties;
import com.sandy.ratelimiter.dto.RateLimitResponse;
import com.sandy.ratelimiter.exception.RateLimitExceededException;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private final RedisService redisService;
    private final RateLimitProperties rateLimitProperties;

    public RateLimiterService(
            RedisService redisService,
            RateLimitProperties rateLimitProperties) {

        this.redisService = redisService;
        this.rateLimitProperties = rateLimitProperties;
    }

    public RateLimitResponse checkRequest(String userId) {

        long limit = rateLimitProperties.getMaxRequests();
        long windowSeconds = rateLimitProperties.getWindowSeconds();

        Long result = redisService.checkRateLimit(
                "rate_limit:" + userId,
                limit,
                windowSeconds
        );

        if (result == 0) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }

        long remaining = limit - result;

        return new RateLimitResponse(true, (int) remaining);
    }
}
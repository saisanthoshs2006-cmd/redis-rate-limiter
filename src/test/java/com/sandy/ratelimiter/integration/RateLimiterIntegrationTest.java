package com.sandy.ratelimiter.integration;

import com.sandy.ratelimiter.config.RateLimitProperties;
import com.sandy.ratelimiter.dto.RateLimitResponse;
import com.sandy.ratelimiter.exception.RateLimitExceededException;
import com.sandy.ratelimiter.service.RateLimiterService;
import com.sandy.ratelimiter.service.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RateLimiterIntegrationTest {

    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RateLimitProperties rateLimitProperties;

    @BeforeEach
    void cleanRedis() {
        redisTemplate.delete("rate_limit:integration-test");
    }

    @Test
    void shouldAllowRequestsUntilLimit() {

        String userId = "integration-test";

        for (int i = 1; i <= 5; i++) {

            RateLimitResponse response =
                    rateLimiterService.checkRequest(userId);

            assertTrue(response.isAllowed());
            assertEquals(5 - i, response.getRemainingRequests());
        }
    }
    @Test
    void shouldSetExpirationOnFirstRequest() {

        String userId = "integration-test";

        rateLimiterService.checkRequest(userId);

        Long ttl = redisTemplate.getExpire(
                "rate_limit:" + userId
        );

        assertTrue(ttl > 0);
        assertTrue(ttl <= 60);
    }

    @Test
    void shouldRejectRequestAfterLimit() {

        String userId = "integration-test";

        for (int i = 0; i < 5; i++) {
            rateLimiterService.checkRequest(userId);
        }

        assertThrows(
                RateLimitExceededException.class,
                () -> rateLimiterService.checkRequest(userId)
        );
    }
}
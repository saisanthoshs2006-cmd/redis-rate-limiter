package com.sandy.ratelimiter;

import com.sandy.ratelimiter.config.RateLimitProperties;
import com.sandy.ratelimiter.dto.RateLimitRequest;
import com.sandy.ratelimiter.dto.RateLimitResponse;
import com.sandy.ratelimiter.service.RateLimiterService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

    private final RateLimiterService rateLimiterService;
    private final RateLimitProperties rateLimitProperties;

    public HelloController(
            RateLimiterService rateLimiterService,
            RateLimitProperties rateLimitProperties) {

        this.rateLimiterService = rateLimiterService;
        this.rateLimitProperties = rateLimitProperties;
    }

    @PostMapping("/api/rate-limit/check")
    public ResponseEntity<RateLimitResponse> check(
            @Valid @RequestBody RateLimitRequest request) {

        RateLimitResponse response =
                rateLimiterService.checkRequest(request.getUserId());

        return ResponseEntity.ok()
                .header(
                        "X-RateLimit-Limit",
                        String.valueOf(rateLimitProperties.getMaxRequests())
                )
                .header(
                        "X-RateLimit-Remaining",
                        String.valueOf(response.getRemainingRequests())
                )
                .body(response);
    }
}
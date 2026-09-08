package com.sandy.ratelimiter.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import com.sandy.ratelimiter.config.RateLimitProperties;

@Component
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitProperties {
    private long maxRequests;
    private long windowSeconds;

    public long getMaxRequests() {
        return maxRequests;
    }

    public void setMaxRequests(long maxRequests) {
        this.maxRequests = maxRequests;
    }

    public long getWindowSeconds() {
        return windowSeconds;
    }

    public void setWindowSeconds(long windowSeconds) {
        this.windowSeconds = windowSeconds;
    }
}

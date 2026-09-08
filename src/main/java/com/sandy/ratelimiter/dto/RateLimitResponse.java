package com.sandy.ratelimiter.dto;

public class RateLimitResponse {

    private boolean allowed;
    private int remainingRequests;

    public RateLimitResponse(boolean allowed, int remainingRequests) {
        this.allowed = allowed;
        this.remainingRequests = remainingRequests;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public int getRemainingRequests() {
        return remainingRequests;
    }
}
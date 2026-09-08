package com.sandy.ratelimiter.dto;

import jakarta.validation.constraints.NotBlank;

public class RateLimitRequest {

    @NotBlank(message = "userId is needed")
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void SetUserId(String userId){
        this.userId=userId;
    }
}

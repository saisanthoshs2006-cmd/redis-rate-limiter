package com.sandy.ratelimiter.controller;

import com.sandy.ratelimiter.HelloController;
import com.sandy.ratelimiter.config.RateLimitProperties;
import com.sandy.ratelimiter.dto.RateLimitResponse;
import com.sandy.ratelimiter.service.RateLimiterService;
import com.sandy.ratelimiter.exception.RateLimitExceededException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
class RateLimitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RateLimiterService rateLimiterService;

    @MockitoBean
    private RateLimitProperties rateLimitProperties;

    @Test
    void shouldRejectEmptyUserId() throws Exception {

        mockMvc.perform(
                        post("/api/rate-limit/check")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "userId": ""
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("userId is needed"));
    }

    @Test
    void shouldReturn429WhenRateLimitExceeded() throws Exception {

        when(rateLimiterService.checkRequest("sai"))
                .thenThrow(new RateLimitExceededException("Rate limit exceeded"));

        mockMvc.perform(
                        post("/api/rate-limit/check")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "userId": "sai"
                                    }
                                    """)
                )
                .andExpect(status().isTooManyRequests())
                .andExpect(
                        MockMvcResultMatchers.header()
                                .string("X-RateLimit-Remaining", "0")
                )
                .andExpect(jsonPath("$.allowed").value(false))
                .andExpect(jsonPath("$.remainingRequests").value(0));
    }
    @Test
    void shouldReturnAllowedResponse() throws Exception {

        when(rateLimiterService.checkRequest("sai"))
                .thenReturn(new RateLimitResponse(true, 4));

        mockMvc.perform(
                        post("/api/rate-limit/check")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "userId": "sai"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.allowed").value(true))
                .andExpect(jsonPath("$.remainingRequests").value(4));
    }
}
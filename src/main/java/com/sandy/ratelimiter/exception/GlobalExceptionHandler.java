package com.sandy.ratelimiter.exception;

import com.sandy.ratelimiter.config.RateLimitProperties;
import com.sandy.ratelimiter.dto.ErrorResponse;
import com.sandy.ratelimiter.dto.RateLimitResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final RateLimitProperties rateLimitProperties;

    public GlobalExceptionHandler(RateLimitProperties rateLimitProperties) {
        this.rateLimitProperties = rateLimitProperties;
    }
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<RateLimitResponse> handleRateLimitExceeded(
            RateLimitExceededException ex) {

        return ResponseEntity
                .status(429)
                .header(
                        "X-RateLimit-Limit",
                        String.valueOf(rateLimitProperties.getMaxRequests())
                )
                .header(
                        "X-RateLimit-Remaining",
                        "0"
                )
                .body(new RateLimitResponse(false, 0));
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex
    ) {
        String message = ex.getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        "VALIDATION_ERROR",
                        message
                ));
    }

}

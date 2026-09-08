package com.sandy.ratelimiter.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisScript<Long> redisLimitScript;

    public RedisService(RedisTemplate<String, String> redisTemplate) {

        this.redisTemplate = redisTemplate;

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/rate_limit.lua"));
        script.setResultType(Long.class);

        this.redisLimitScript = script;
    }

//    public void setValue(String key, String value) {
//        redisTemplate.opsForValue().set(key, value);
//    }
//
//    public Long incrementValue(String key) {
//        return redisTemplate.opsForValue().increment(key);
//    }
//
//    public void expire(String key, long seconds) {
//        redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
//    }

    public Long checkRateLimit(String key, long limit, long windowSeconds) {

        return redisTemplate.execute(
                redisLimitScript,
                java.util.Collections.singletonList(key),
                String.valueOf(limit),
                String.valueOf(windowSeconds)
        );
    }
}
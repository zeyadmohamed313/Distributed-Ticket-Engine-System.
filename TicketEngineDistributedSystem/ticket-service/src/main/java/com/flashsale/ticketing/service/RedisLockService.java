package com.flashsale.ticketing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisLockService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean acquireLock(String key, long ttlInSeconds) {
        // SETNX: Set if Not Exists - atomic operation
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "LOCKED", Duration.ofSeconds(ttlInSeconds));

        return Boolean.TRUE.equals(success);
    }

    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }
}
package com.api.readinglog.common.redis.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;


    public void setData(String key, Object value, Long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value.toString(), time, timeUnit);
    }

    public Object getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }


    public void deleteData(String key) {
        redisTemplate.delete(key);
    }


    public void increaseData(String key) {
        redisTemplate.opsForValue().increment(key);
    }

}

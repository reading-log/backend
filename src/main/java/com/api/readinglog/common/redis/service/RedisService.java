package com.api.readinglog.common.redis.service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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

    public void setLikeData(String userKey, Long summaryId) {
        redisTemplate.opsForSet().add(userKey, summaryId.toString());
    }

    public Object getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    public void deleteLikeData(String userKey, Long summaryId) {
        redisTemplate.opsForSet().remove(userKey, summaryId.toString());
    }

    public void increaseLikeCount(String key) {
        redisTemplate.opsForValue().increment(key, 1);
    }

    public void decreaseLikeCount(String key) {
        Integer currentLikeCount = getLikeCount(key);

        if (currentLikeCount == null || currentLikeCount <= 0) {
            return;
        }
        redisTemplate.opsForValue().increment(key, -1);
    }

    public boolean isPresent(String userKey, Long summaryId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userKey, summaryId.toString()));
    }

    public Integer getLikeCount(String key) {
        Object result = redisTemplate.opsForValue().get(key);
        return result != null ? (Integer) result : 0;
    }

}

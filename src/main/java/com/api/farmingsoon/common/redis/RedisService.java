package com.api.farmingsoon.common.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    public void initialize() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
    public void setData(String key, Object value, Long time,TimeUnit timeUnit) {
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

    public Set<String> getKeySet(String domain) {
        return redisTemplate.keys(domain);
    }

    public void addToSet(String key, Long itemId){
        redisTemplate.opsForSet().add(key,itemId);
    }
    public boolean isExistInSet(String key, Long itemId){
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, itemId));
    }

}

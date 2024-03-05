package com.api.farmingsoon.common.redis;

import com.api.farmingsoon.common.util.TimeUtils;
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

    public boolean isNotExistsKey(String key){
        return Boolean.FALSE.equals(redisTemplate.hasKey(key));
    }

    public void addToSet(String key, String value){
            redisTemplate.opsForSet().add(key,value);
    }
    public void setExpireTime(String key, Long ttl){
        redisTemplate.expire(key, ttl , TimeUnit.SECONDS); // 만료기간 설정
    }
    public void createSet(String key, String value){
        redisTemplate.opsForSet().add(key, value); // set생성
    }
    public void addToSet(String key,Long ttl, String value){
        redisTemplate.opsForSet().add(key,value);

    }

    public boolean isNotExistInSet(String key, Long itemId){
        return Boolean.FALSE.equals(redisTemplate.opsForSet().isMember(key, String.valueOf(itemId)));
    }

}

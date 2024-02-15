package com.api.farmingsoon.domain.item.service;

import com.api.farmingsoon.common.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ItemRedisService {
    private final RedisService redisService;

    public void setBidEndTime(Long itemId, Integer expire){
        redisService.setData(String.valueOf(itemId), "", Long.valueOf(expire), TimeUnit.DAYS);
    }
}

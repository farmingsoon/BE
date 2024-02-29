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
        redisService.setData("bidEnd_" + String.valueOf(itemId), "", Long.valueOf(expire), TimeUnit.DAYS);
    }


     // @Description 중복된 접근이 아니라면 조회수를 증가시키고 접근 처리
    public void handleViewCount(String cookieValueOfViewer, Long itemId) {
        if (!redisService.isExistInSet(cookieValueOfViewer, itemId))
        {
            redisService.increaseData("viewCount_item_" + itemId);
            redisService.addToSet(cookieValueOfViewer, itemId);
        }
    }
}

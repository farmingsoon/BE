package com.api.farmingsoon.domain.item.service;

import com.api.farmingsoon.common.redis.RedisService;
import com.api.farmingsoon.common.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ItemRedisService {
    private final RedisService redisService;

    public void setBidEndTime(Long itemId, Integer expire){
        redisService.setData("bidEnd_" + String.valueOf(itemId), "", Long.valueOf(expire), TimeUnit.DAYS);
    }


    /**
     *     @Description
     *     1. 중복된 접근이 아니라면 조회수를 증가시키고 접근 처리(Set 의 키 값이 없거나 set 내부에 value 가 없거나)
     *     2. set 이 없다면 만들고 만료기간 자정으로 설정
     *     3. 있다면 set 에 value 추가
     */

     @Async("testExecutor")
     public void handleViewCount(String cookieValueOfViewer, Long itemId) {
         if (redisService.isNotExistInSet(cookieValueOfViewer, String.valueOf(itemId)))
         {
             redisService.increaseData("viewCount_item_" + itemId);
             if(redisService.isNotExistsKey(cookieValueOfViewer))
             {
                 redisService.createSet(cookieValueOfViewer, String.valueOf(itemId));
                 redisService.setExpireTime(cookieValueOfViewer, TimeUtils.getRemainingTimeUntilMidnight());
             }
             else
             {
                 redisService.addToSet(cookieValueOfViewer, String.valueOf(itemId));
             }
         }
     }
}

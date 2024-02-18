package com.api.farmingsoon.common.scheduler;

import com.api.farmingsoon.common.redis.RedisService;
import com.api.farmingsoon.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Component
@EnableScheduling
public class SchedulerRunner {
    private final RedisService redisService;
    private final ItemService itemService;

    @Scheduled(fixedDelay = 30000)
    public void updateViewCount() {
        Set<String> keySet = redisService.getKeySet("viewCount*");
        for (String key : keySet) {
            String[] splitKey = key.split("_"); // 0 : viewCount, 1 : Domain명, 2 : Entity_Id

            itemService.increaseViewCount(Long.parseLong(splitKey[2]), Integer.parseInt((String) redisService.getData(key))); // itemId, 조회수
                redisService.deleteData(key);
        }
    }
}


package com.api.farmingsoon.common.scheduler;

import com.api.farmingsoon.common.redis.RedisService;
import com.api.farmingsoon.domain.item.service.ItemService;
import io.lettuce.core.RedisCommandTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
@EnableScheduling
public class SchedulerRunner {
    private final RedisService redisService;
    private final ItemService itemService;

    @Scheduled(fixedDelay = 30000)
    public void updateViewCount() {
        try {
            Set<String> keySet = redisService.getKeySet("viewCount*");
            for (String key : keySet) {
                String[] splitKey = key.split("_"); // 0 : viewCount, 1 : Domain명, 2 : Entity_Id
                log.info("increase viewCount Item_"+ splitKey[2] + " : " + Integer.parseInt((String) redisService.getData(key)));

                itemService.increaseViewCount(Long.parseLong(splitKey[2]), Integer.parseInt((String) redisService.getData(key))); // itemId, 조회수
                    redisService.deleteData(key);

            }

        }catch (RedisCommandTimeoutException exception)
        {
            log.info("redisCommandTimeoutException");
        }
        catch (QueryTimeoutException exception)
        {
            log.info("queryTimeoutException");
        }
    }
}


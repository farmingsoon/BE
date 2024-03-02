package com.api.farmingsoon.common.redis.listener;

import com.api.farmingsoon.common.redis.RedisService;
import com.api.farmingsoon.common.redis.event.RedisResponseDelayEvent;
import com.api.farmingsoon.common.redis.event.RedisRestartEvent;
import com.api.farmingsoon.common.scheduler.CustomSchedulerRunner;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisEventListener {
    private final CustomSchedulerRunner schedulerRunner;
    private final RedisService redisService;
    private final ItemService itemService;

    @EventListener
    public void runByRedisResponseDelay(RedisResponseDelayEvent event){
        schedulerRunner.runRestartMonitoringScheduler();
        schedulerRunner.runBidEndScheduler();
        schedulerRunner.stopRedisHealthCheckScheduler();
    }

    @EventListener
    @Async("testExecutor")
    public void runByRedisRestart(RedisRestartEvent event) {
        schedulerRunner.stopRestartMonitoringScheduler();
        schedulerRunner.stopBidEndScheduler();
        schedulerRunner.runRedisHealthCheckScheduler();


        List<Item> biddingItemIdList = itemService.findBiddingItemList();

        biddingItemIdList.forEach(item -> redisService.setData(
                "bidEnd_" + item.getId(),
                "",
                Duration.between(LocalDateTime.now(), item.getExpiredAt()).getSeconds(),
                TimeUnit.SECONDS));
    }

}

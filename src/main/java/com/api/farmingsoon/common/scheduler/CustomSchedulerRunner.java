package com.api.farmingsoon.common.scheduler;

import com.api.farmingsoon.common.redis.RedisService;
import com.api.farmingsoon.common.redis.event.RedisResponseDelayEvent;
import com.api.farmingsoon.common.redis.event.RedisRestartEvent;
import com.api.farmingsoon.domain.item.event.BidEndSchedulerRunEvent;
import com.api.farmingsoon.domain.item.service.ItemService;
import io.lettuce.core.RedisCommandTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomSchedulerRunner {
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final RedisConnectionFactory redisConnectionFactory;
    private final ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private TaskScheduler taskScheduler;

    /**
     * @Description
     * 주기적으로 Redis의 상태 체크를 합니다.
     * 만약 응답이 지연된다면 Redis에 장애가 있다고 판단하여 RedisEventListener에서 처리될 RedisResponseDelayEvent를 발행합니다.
     * RedisEventListener에서는 마감처리를 진행할 bidEndScheduler와 redis의 재연결을 모니터링하는 restartScheduler를 실행합니다.
     */
    public void runRedisHealthCheckScheduler(){
        ScheduledFuture<?> task = taskScheduler.scheduleAtFixedRate(
                ()->{
                    log.info("runRedisHealthCheckScheduler");
                    RedisConnection connection = redisConnectionFactory.getConnection();
                    try {
                        connection.ping();
                    }catch (QueryTimeoutException ex)
                    {
                        applicationEventPublisher.publishEvent(new RedisResponseDelayEvent());
                    }
                    finally {
                        connection.close();
                    }
                }
                , 30000); // ms
        scheduledTasks.put("RedisHealthCheckScheduler", task);
    }
    /**
     * @Description
     * redis의 응답 지연으로 Redis에 장애가 있다고 판단될 시 실행되는 스케줄러로 주기적으로 ping을 날려 재연결을 모니터링합니다.
     * 만약 응답으로 PONG을 받는다면 재연결됐다고 판단하여 redisEventListener에서 실행되는 RedisRestartEvent를 발행합니다.
     * 리스너에서는 다시 HealthCheckScheduler를 실행시킵니다.
     * 또한, 아직 입찰마감 처리까지 시간이 남았지만 레디스에서 사라진 데이터들을 채워넣습니다.
     * 이후, BidEndScheduler와 runRestartMonitoringScheduler를 종료시킵니다.
     */
    public void runRestartMonitoringScheduler(){
        ScheduledFuture<?> task = taskScheduler.scheduleAtFixedRate(
                ()->{
                    log.info("runRestartMonitoringScheduler");
                    RedisConnection connection = redisConnectionFactory.getConnection();

                    try {
                        if(connection.ping().equals("PONG")){
                            applicationEventPublisher.publishEvent(new RedisRestartEvent());
                        }
                    }catch (QueryTimeoutException ex)
                    {
                        log.info("재시작 대기중");
                    }
                    finally {
                        connection.close();
                    }
                }
                , 30000); // ms
        scheduledTasks.put("RestartMonitoringScheduler", task);
    }

    /**
     * @Description
     * 현재시간 기준으로 만료일자가 지났지만 입찰중인 상품들을 마감처리합니다.
     */
    public void runBidEndScheduler(){
        ScheduledFuture<?> task = taskScheduler.scheduleAtFixedRate(
                ()->{
                    applicationEventPublisher.publishEvent(new BidEndSchedulerRunEvent());
                }
                , 5000); // ms
        scheduledTasks.put("BidEndScheduler", task);
    }
    public void stopRestartMonitoringScheduler(){
        log.info("stopRestartMonitoringScheduler");
        scheduledTasks.get("RestartMonitoringScheduler").cancel(true);
    }

    public void stopRedisHealthCheckScheduler(){
        log.info("stopRedisHealthCheckScheduler");
        scheduledTasks.get("RedisHealthCheckScheduler").cancel(true);
    }
    public void stopBidEndScheduler(){
        log.info("stopBidEndScheduler");
        scheduledTasks.get("BidEndScheduler").cancel(true);
    }
}

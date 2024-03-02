package com.api.farmingsoon.common.init;

import com.api.farmingsoon.common.scheduler.CustomSchedulerRunner;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class InitData {
    private final CustomSchedulerRunner schedulerRunner;

    @Bean
    public CommandLineRunner init(){
        return args -> {

            schedulerRunner.runRedisHealthCheckScheduler();

        };
    }
}

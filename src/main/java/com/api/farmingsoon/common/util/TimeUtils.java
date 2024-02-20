package com.api.farmingsoon.common.util;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
public class TimeUtils {

    public static LocalDateTime setExpireAt(Integer period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime plusNDay = now.plusDays(period);
        return plusNDay;
        /*
        return LocalDateTime.of(
                plusNDay.getYear(),
                plusNDay.getMonth(),
                plusNDay.getDayOfMonth(),
                plusNDay.getHour(),
                plusNDay.getMinute(),
                plusNDay.getSecond());
        */
    }
    public static long getRemainingTimeUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();

        // 현재 시간부터 내일 00:00:00까지의 남은 시간 계산
        Duration duration = Duration.between(now, now.plusDays(1).withHour(0).withMinute(0).withSecond(0));

        // 계산된 시간을 초로 변환
        long remainingSeconds = duration.getSeconds();

        log.info("다음 날까지의 남은 시간(초): " + remainingSeconds);
        return remainingSeconds;
    }
}

package com.api.farmingsoon.common.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
}

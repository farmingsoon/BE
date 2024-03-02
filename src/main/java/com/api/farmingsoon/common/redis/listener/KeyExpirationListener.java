package com.api.farmingsoon.common.redis.listener;

import com.api.farmingsoon.domain.item.event.BidEndKeyExpiredEvent;
import com.api.farmingsoon.domain.notification.event.ChatNotificationDebounceKeyExpiredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KeyExpirationListener extends KeyExpirationEventMessageListener {

    private final ApplicationEventPublisher applicationEventPublisher;
    public KeyExpirationListener(RedisMessageListenerContainer listenerContainer, ApplicationEventPublisher applicationEventPublisher) {
        super(listenerContainer);

        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void onMessage(Message key, byte[] pattern) {
        log.info("key_expired : " + key.toString());
        String[] expiredKey = key.toString().split("_");
        if (expiredKey[0].equals("bidEnd")) {
            applicationEventPublisher.publishEvent(new BidEndKeyExpiredEvent((Long.valueOf(expiredKey[1]))));  ; // itemId
        } else if (expiredKey[0].equals("chatting")) { // memberId
            applicationEventPublisher.publishEvent(new ChatNotificationDebounceKeyExpiredEvent(Long.valueOf(expiredKey[1]))); // receiverId
        }
    }
}
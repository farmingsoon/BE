package com.api.farmingsoon.domain.item.listener;

import com.api.farmingsoon.domain.item.service.ItemService;
import com.api.farmingsoon.domain.notification.model.Notification;
import com.api.farmingsoon.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class BidEndListener extends KeyExpirationEventMessageListener {
    private final ItemService itemService;
    public BidEndListener(RedisMessageListenerContainer listenerContainer, ItemService itemService) {
        super(listenerContainer);
        this.itemService = itemService;
    }

    @Override
    public void onMessage(Message key, byte[] pattern) {
        itemService.bidEnd(Long.valueOf(key.toString()));
    }
}

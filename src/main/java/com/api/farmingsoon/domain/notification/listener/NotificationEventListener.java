package com.api.farmingsoon.domain.notification.listener;

import com.api.farmingsoon.domain.notification.event.BidEndEvent;
import com.api.farmingsoon.domain.notification.event.BidRegisterEvent;
import com.api.farmingsoon.domain.notification.event.ItemSoldOutEvent;
import com.api.farmingsoon.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class NotificationEventListener {
    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void bidRegisterNotification(BidRegisterEvent event)
    {
        notificationService.createAndSendNewBidNotification(event.getItemId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void itemSoldOutNotification(ItemSoldOutEvent event)
    {
        notificationService.createAndSendSoldOutNotification(event.getItemId(), event.getBuyerId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void bidEndNotification(BidEndEvent event)
    {
        notificationService.createAndSendBidEndNotification(event.getItem());
    }
}

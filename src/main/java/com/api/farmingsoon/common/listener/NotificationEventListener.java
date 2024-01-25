package com.api.farmingsoon.common.listener;

import com.api.farmingsoon.common.event.BidEndEvent;
import com.api.farmingsoon.common.event.BidRegisterEvent;
import com.api.farmingsoon.common.event.ItemSoldOutEvent;
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
        notificationService.createAndSendNewBidNotification(event.getItem());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void itemSoldOutNotification(ItemSoldOutEvent event)
    {
        notificationService.createAndSendNewBidNotification(event.getItem());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void bidEndNotification(BidEndEvent event)
    {
        notificationService.createAndSendNewBidNotification(event.getItem());
    }
}

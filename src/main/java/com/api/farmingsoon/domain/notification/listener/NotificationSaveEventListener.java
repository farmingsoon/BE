package com.api.farmingsoon.domain.notification.listener;

import com.api.farmingsoon.common.sse.SseService;
import com.api.farmingsoon.domain.notification.event.NotificationSaveEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class NotificationSaveEventListener {

    private  final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotification(NotificationSaveEvent event)
    {
        event.getSenderIdList().forEach(sender -> sseService.sendToClient(sender, event.getMessage()));
    }
}

package com.api.farmingsoon.domain.notification.listener;

import com.api.farmingsoon.common.sse.SseService;
import com.api.farmingsoon.domain.notification.event.NotificationSaveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class NotificationSaveEventListener {

    private  final SseService sseService;

    @Async("testExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotification(NotificationSaveEvent event) throws InterruptedException {
        event.getSenderIdList().forEach(sender -> sseService.sendToClient("NOTIFICATION", sender, event.getMessage()));
    }
}

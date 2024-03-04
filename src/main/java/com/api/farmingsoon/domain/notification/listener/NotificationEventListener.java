package com.api.farmingsoon.domain.notification.listener;

import com.api.farmingsoon.common.sse.SseService;
import com.api.farmingsoon.domain.notification.event.ChatNotificationDebounceKeyExpiredEvent;
import com.api.farmingsoon.domain.notification.event.NotificationSaveEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
@Slf4j
public class NotificationEventListener {

    private  final SseService sseService;

    @Async("testExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotification(NotificationSaveEvent event) throws InterruptedException {
        event.getSenderIdList().forEach(sender -> sseService.sendToClient("NOTIFICATION", sender, event.getMessage()));
        log.info("알림 전송");
    }

    @EventListener
    public void sendChatNotification(ChatNotificationDebounceKeyExpiredEvent event) throws InterruptedException {
        sseService.sendToClient("CHATTING", event.getReceiverId(), "새로운 채팅 메시지가 있습니다.");
        log.info("채팅 알림 전송");
    }
}

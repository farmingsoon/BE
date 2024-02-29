package com.api.farmingsoon.domain.chat.listener;

import com.api.farmingsoon.common.sse.SseService;
import com.api.farmingsoon.domain.chat.event.ChatSaveEvent;
import com.api.farmingsoon.domain.notification.event.NotificationSaveEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
@RequiredArgsConstructor
@Component
public class ChatEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final SseService sseService;
    @Async("testExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendNotification(ChatSaveEvent event) throws InterruptedException {
        messagingTemplate.convertAndSend("/sub/chat-room/" + event.getChatRoomId(), event.getChatResponse());
        sseService.sendToClient("CHATTING", event.getReceiverId(), "새로운 채팅이 있습니다.");
    }
}
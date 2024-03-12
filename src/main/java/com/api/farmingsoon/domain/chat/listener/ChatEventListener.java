package com.api.farmingsoon.domain.chat.listener;

import com.api.farmingsoon.common.redis.RedisService;
import com.api.farmingsoon.common.sse.SseService;
import com.api.farmingsoon.common.util.TimeUtils;
import com.api.farmingsoon.domain.chat.event.ChatSaveEvent;
import com.api.farmingsoon.domain.notification.event.NotificationSaveEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class ChatEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisService redisService;

    @Async("testExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendChatAndDebounceNotification(ChatSaveEvent event) throws InterruptedException {
        messagingTemplate.convertAndSend("/sub/chat-room/" + event.getChatRoomId(), event.getChatResponse());

        if(redisService.isNotExistsKey("debouncing_" + event.getReceiverId())) // 수신자 알림 디바운싱
            redisService.setData("debouncing_" + event.getReceiverId(),"", 2L,TimeUnit.SECONDS);
        if(redisService.isNotExistsKey("debouncing_" + event.getChatResponse().getSenderId())) // 발신자 알림 디바운싱
            redisService.setData("debouncing_" + event.getChatResponse().getSenderId(),"", 2L,TimeUnit.SECONDS);
    }
}
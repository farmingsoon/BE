package com.api.farmingsoon.domain.chatroom.listener;

import com.api.farmingsoon.common.sse.SseService;
import com.api.farmingsoon.domain.chat.service.ChatService;
import com.api.farmingsoon.domain.chatroom.event.ChatRoomConnectEvent;
import com.api.farmingsoon.domain.chatroom.service.ChatRoomRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatRoomEventListener {
    private final ChatService chatService;
    private final ChatRoomRedisService chatRoomRedisService;
    private final SseService sseService;

    @EventListener
    public void readAllChatAndSaveConnectMember(ChatRoomConnectEvent event){
        chatRoomRedisService.connectChatRoom(event.getChatRoomId(), event.getSessionId());
        chatService.readAllMyNotReadChatList(event.getChatRoomId(), event.getMemberId());
        // @Todo 웹소켓으로 채팅방에 상대방이 들어왔음을 알림
    }

    @EventListener
    public void deleteConnectMember(ChatRoomConnectEvent event){
        chatRoomRedisService.disConnectChatRoom(event.getChatRoomId(), event.getSessionId());
    }

}

package com.api.farmingsoon.domain.chatroom.listener;

import com.api.farmingsoon.domain.chat.service.ChatService;
import com.api.farmingsoon.domain.chatroom.event.ChatRoomConnectEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatRoomEventListener {
    private final ChatService chatService;

    @EventListener
    public void readAllChatMessage(ChatRoomConnectEvent event){
        chatService.readAllMyNotReadChatList(event.getChatRoomId(), event.getMemberId());
    }

}

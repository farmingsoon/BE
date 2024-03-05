package com.api.farmingsoon.domain.chatroom.listener;

import com.api.farmingsoon.common.redis.RedisService;
import com.api.farmingsoon.domain.chat.service.ChatService;
import com.api.farmingsoon.domain.chatroom.event.ChatRoomConnectEvent;
import com.api.farmingsoon.domain.chatroom.service.ChatRoomRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatRoomEventListener {
    private final ChatService chatService;
    private final ChatRoomRedisService chatRoomRedisService;

    @EventListener
    public void readAllChatAndSaveConnectMember(ChatRoomConnectEvent event){
        chatRoomRedisService.ConnectChatRoom(event.getChatRoomId(), event.getSessionId());
        chatService.readAllMyNotReadChatList(event.getChatRoomId(), event.getMemberId());
    }

}

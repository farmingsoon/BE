package com.api.farmingsoon.domain.chatroom.listener;

import com.api.farmingsoon.common.sse.SseService;
import com.api.farmingsoon.domain.chat.dto.ChattingConnectResponse;
import com.api.farmingsoon.domain.chat.service.ChatService;
import com.api.farmingsoon.domain.chatroom.event.ChatRoomConnectEvent;
import com.api.farmingsoon.domain.chatroom.event.ChatRoomDisConnectEvent;
import com.api.farmingsoon.domain.chatroom.service.ChatRoomRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ChatRoomEventListener {
    private final ChatService chatService;
    private final ChatRoomRedisService chatRoomRedisService;
    private final SimpMessagingTemplate messagingTemplate;
    private  final SseService sseService;

    /**
     * @Description
     * 1. 채팅방에 연결됐음을 저장
     * 2. 연결된 사람의 채팅방에 안읽었던 메시지를 모두 읽음 처리
     * 3. 채팅방에 상대방이 연결되었음을 알리는 알림
     */
    @EventListener
    public void readAllChatAndSaveConnectMember(ChatRoomConnectEvent event){
        chatRoomRedisService.connectChatRoom(event.getChatRoomId(), event.getSessionId());
        chatService.readAllMyNotReadChatList(event.getChatRoomId(), event.getConnectMemberId());
        messagingTemplate.convertAndSend("/sub/chat-room/" + event.getChatRoomId(), new ChattingConnectResponse(event.getConnectMemberId()));
        sseService.sendToClient("CHATROOM_UPDATE", event.getConnectMemberId(), "채팅방 목록을 업데이트 해주세요.");
    }

    @EventListener
    public void deleteConnectMember(ChatRoomDisConnectEvent event){
        chatRoomRedisService.disConnectChatRoom(event.getChatRoomId(), event.getSessionId());
    }

}

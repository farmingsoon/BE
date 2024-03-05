package com.api.farmingsoon.domain.chatroom.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatRoomDisConnectEvent {

    private Long chatRoomId;
    private String sessionId;

    @Builder
    private ChatRoomDisConnectEvent(Long chatRoomId, String sessionId) {
        this.chatRoomId = chatRoomId;
        this.sessionId = sessionId;
    }
}

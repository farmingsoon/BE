package com.api.farmingsoon.domain.chatroom.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatRoomConnectEvent {

    private Long connectMemberId;
    private Long chatRoomId;
    private String sessionId;

    @Builder
    private ChatRoomConnectEvent(Long connectMemberId, Long chatRoomId, String sessionId) {
        this.connectMemberId = connectMemberId;
        this.chatRoomId = chatRoomId;
        this.sessionId = sessionId;
    }
}

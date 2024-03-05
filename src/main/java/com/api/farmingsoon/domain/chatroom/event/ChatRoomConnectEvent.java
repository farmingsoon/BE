package com.api.farmingsoon.domain.chatroom.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatRoomConnectEvent {

    private Long memberId;
    private Long chatRoomId;
    private String sessionId;

    @Builder
    private ChatRoomConnectEvent(Long memberId, Long chatRoomId, String sessionId) {
        this.memberId = memberId;
        this.chatRoomId = chatRoomId;
        this.sessionId = sessionId;
    }
}

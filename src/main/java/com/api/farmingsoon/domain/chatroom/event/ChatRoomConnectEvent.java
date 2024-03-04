package com.api.farmingsoon.domain.chatroom.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatRoomConnectEvent {

    private Long memberId;
    private Long chatRoomId;

    @Builder
    private ChatRoomConnectEvent(Long memberId, Long chatRoomId) {
        this.memberId = memberId;
        this.chatRoomId = chatRoomId;
    }
}

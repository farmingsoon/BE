package com.api.farmingsoon.domain.chat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageRequest {
    private Long chatRoomId;
    private Long senderId;
    private String message;

    @Builder
    public ChatMessageRequest(Long chatRoomId,Long senderId, String message) {
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
        this.message = message;
    }
}

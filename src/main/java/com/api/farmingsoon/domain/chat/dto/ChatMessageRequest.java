package com.api.farmingsoon.domain.chat.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageRequest {
    private Long chatRoomId;
    private String message;

    @Builder
    public ChatMessageRequest(Long chatRoomId, String message) {
        this.chatRoomId = chatRoomId;
        this.message = message;
    }
}

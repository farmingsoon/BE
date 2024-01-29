package com.api.farmingsoon.domain.chat.dto;

import com.api.farmingsoon.domain.chat.model.Chat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ChatResponse {

    private Long roomId;
    private String message;
    private String sender;
    private LocalDateTime createAt;

    @Builder
    private ChatResponse(String sender, Long roomId, String message, LocalDateTime createAt) {
        this.sender = sender;
        this.roomId = roomId;
        this.message = message;
        this.createAt = createAt;
    }
    public static ChatResponse of(Chat chat) {
        return ChatResponse
                .builder()
                .sender(chat.getSender().getEmail())
                .roomId(chat.getChatRoom().getId())
                .message(chat.getMessage())
                .createAt(chat.getCreatedAt())
                .build();
    }
}

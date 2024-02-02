package com.api.farmingsoon.domain.chat.dto;

import com.api.farmingsoon.domain.chat.model.Chat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ChatResponse {

    private String message;
    private String sender;
    private LocalDateTime createAt;

    @Builder
    private ChatResponse(String sender, String message, LocalDateTime createAt) {
        this.sender = sender;
        this.message = message;
        this.createAt = createAt;
    }
    public static ChatResponse of(Chat chat) {
        return ChatResponse
                .builder()
                .sender(chat.getSender().getEmail())
                .message(chat.getMessage())
                .createAt(chat.getCreatedAt())
                .build();
    }
}

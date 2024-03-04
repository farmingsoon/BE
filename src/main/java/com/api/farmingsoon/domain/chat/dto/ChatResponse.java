package com.api.farmingsoon.domain.chat.dto;

import com.api.farmingsoon.domain.chat.model.Chat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ChatResponse {

    private String message;
    private Long chatId;
    private Long senderId;
    private LocalDateTime createAt;

    @Builder
    private ChatResponse(Long senderId, String message,Long chatId, LocalDateTime createAt) {
        this.senderId = senderId;
        this.chatId = chatId;
        this.message = message;
        this.createAt = createAt;
    }
    public static ChatResponse of(Chat chat) {
        return ChatResponse
                .builder()
                .senderId(chat.getSender().getId())
                .chatId(chat.getId())
                .message(chat.getMessage())
                .createAt(chat.getCreatedAt())
                .build();
    }
}

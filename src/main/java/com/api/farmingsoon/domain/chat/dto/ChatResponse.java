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
    private Boolean isRead;
    private LocalDateTime createAt;
    private final String type = "SEND";

    @Builder
    private ChatResponse(Long senderId, String message, Boolean isRead, Long chatId, LocalDateTime createAt) {
        this.senderId = senderId;
        this.chatId = chatId;
        this.message = message;
        this.isRead = isRead;
        this.createAt = createAt;
    }
    public static ChatResponse of(Chat chat) {
        return ChatResponse
                .builder()
                .senderId(chat.getSender().getId())
                .chatId(chat.getId())
                .message(chat.getMessage())
                .isRead(chat.getIsRead())
                .createAt(chat.getCreatedAt())
                .build();
    }
}

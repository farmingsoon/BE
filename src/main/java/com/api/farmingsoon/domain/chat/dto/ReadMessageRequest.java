package com.api.farmingsoon.domain.chat.dto;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReadMessageRequest {
    private Long chatId;

    public ReadMessageRequest(Long chatId) {
        this.chatId = chatId;
    }
}

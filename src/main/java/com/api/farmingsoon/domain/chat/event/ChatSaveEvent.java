package com.api.farmingsoon.domain.chat.event;

import com.api.farmingsoon.domain.chat.dto.ChatResponse;
import com.api.farmingsoon.domain.chatroom.dto.ChatRoomResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class ChatSaveEvent {
    private Long chatRoomId;
    private Long receiverId;
    private ChatResponse chatResponse;

    @Builder
    private ChatSaveEvent(Long chatRoomId, Long receiverId, ChatResponse chatResponse) {
        this.chatRoomId = chatRoomId;
        this.receiverId = receiverId;
        this.chatResponse = chatResponse;
    }
}

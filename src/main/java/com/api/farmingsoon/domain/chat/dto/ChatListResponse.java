package com.api.farmingsoon.domain.chat.dto;

import com.api.farmingsoon.common.pagenation.Pagination;
import com.api.farmingsoon.domain.chat.model.Chat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
public class ChatListResponse {

    private List<ChatResponse> chats;
    private Pagination<ChatResponse> pagination;

    @Builder
    public ChatListResponse(List<ChatResponse> chats, Pagination<ChatResponse> pagination) {
        this.chats = chats;
        this.pagination = pagination;
    }
    
    public static ChatListResponse of(Page<Chat> chatPage)
    {
        Page<ChatResponse> chatDtoPage = chatPage.map(ChatResponse::of);
        return ChatListResponse.builder()
                .chats(chatDtoPage.getContent())
                .pagination(Pagination.of(chatPage))
                .build();
    }
}

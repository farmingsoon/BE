package com.api.farmingsoon.domain.chatroom.dto;

import com.api.farmingsoon.domain.chat.dto.ChatResponse;
import com.api.farmingsoon.domain.chat.model.Chat;
import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomDetailResponse {

    private Long toUserId;

    private String itemTitle;

    // private String itemImage or thumbnailImage;

    private Long price;

    private Long itemId;

    private List<ChatResponse> chatList;

    @Builder
    private ChatRoomDetailResponse(Long toUserId, String itemTitle, Long price, Long itemId, List<ChatResponse> chatList) {
        this.toUserId = toUserId;
        this.itemTitle = itemTitle;
        this.price = price;
        this.itemId = itemId;
        this.chatList = chatList;
    }

    public static ChatRoomDetailResponse of(ChatRoom chatRoom, String fromUsername, List<ChatResponse> chatList) {
        return ChatRoomDetailResponse.builder()
                .toUserId(ChatRoom.resolveToMember(chatRoom, fromUsername).getId())
                .itemTitle(chatRoom.getItem().getTitle())
                .price(chatRoom.getItem().getHopePrice())
                .itemId(chatRoom.getItem().getId())
                .chatList(chatList)
                .build();
    }
}

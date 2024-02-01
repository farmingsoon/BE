package com.api.farmingsoon.domain.chatroom.dto;

import com.api.farmingsoon.domain.chat.dto.ChatResponse;
import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.member.model.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomDetailResponse {

    private Long itemId; // 아이템 상세

    private String itemTitle;

    private String itemThumbnailImage;

    private Long hopePrice;

    private String toUsername; // 상대방 식별

    private String toUserProfileImage;


    @Builder
    private ChatRoomDetailResponse(String toUsername, String itemTitle, Long hopePrice,String toUserProfileImage , String itemThumbnailImage,Long itemId) {
        this.toUsername = toUsername;
        this.itemTitle = itemTitle;
        this.hopePrice = hopePrice;
        this.itemThumbnailImage = itemThumbnailImage;
        this.toUserProfileImage = toUserProfileImage;
        this.itemId = itemId;
    }

    public static ChatRoomDetailResponse of(ChatRoom chatRoom, String fromUsername) {
        Member toUser = ChatRoom.resolveToMember(chatRoom, fromUsername);
        return ChatRoomDetailResponse.builder()
                .toUsername(toUser.getEmail())
                .itemTitle(chatRoom.getItem().getTitle())
                .hopePrice(chatRoom.getItem().getHopePrice())
                .itemId(chatRoom.getItem().getId())
                .itemThumbnailImage(chatRoom.getItem().getThumbnailImageUrl())
                .toUserProfileImage(toUser.getProfileImg())
                .build();
    }
}

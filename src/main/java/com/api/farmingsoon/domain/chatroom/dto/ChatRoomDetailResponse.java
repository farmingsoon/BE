package com.api.farmingsoon.domain.chatroom.dto;

import com.api.farmingsoon.domain.bid.model.Bid;
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

    private Integer highestPrice;

    private String toUserProfileImage;


    @Builder
    private ChatRoomDetailResponse(String itemTitle, Integer highestPrice,String toUserProfileImage , String itemThumbnailImage,Long itemId) {
        this.itemTitle = itemTitle;
        this.highestPrice = highestPrice;
        this.itemThumbnailImage = itemThumbnailImage;
        this.toUserProfileImage = toUserProfileImage;
        this.itemId = itemId;
    }

    public static ChatRoomDetailResponse of(ChatRoom chatRoom, String fromUsername) {
        Member toUser = ChatRoom.resolveToMember(chatRoom, fromUsername);
        return ChatRoomDetailResponse.builder()
                .itemTitle(chatRoom.getItem().getTitle())
                .highestPrice(chatRoom.getItem().getBidList().stream().map(Bid::getPrice).max(Integer::compareTo).orElse(null))
                .itemId(chatRoom.getItem().getId())
                .itemThumbnailImage(chatRoom.getItem().getThumbnailImageUrl())
                .toUserProfileImage(toUser.getProfileImg())
                .build();
    }
}

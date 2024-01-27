package com.api.farmingsoon.domain.chatroom.dto;

import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.member.model.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomResponse {

    private Long chatRoomId;

    private Long toUserId;

    private String toUserName;

    private String itemTitle;

    @Builder
    public ChatRoomResponse(Long chatRoomId, Long toUserId, String toUserName, String itemTitle){
        this.chatRoomId = chatRoomId;
        this.toUserId = toUserId;
        this.toUserName = toUserName;
        this.itemTitle = itemTitle;
    }

    public static ChatRoomResponse of(ChatRoom chatRoom, String fromName, String itemTitle) {
        Member toMember =  ChatRoom.resolveToMember(chatRoom, fromName);
        return ChatRoomResponse.builder()
                .chatRoomId(chatRoom.getId())
                .toUserId(toMember.getId())
                .toUserName(toMember.getEmail())
                .itemTitle(itemTitle)
                .build();
    }
}
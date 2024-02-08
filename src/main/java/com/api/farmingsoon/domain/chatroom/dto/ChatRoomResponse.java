package com.api.farmingsoon.domain.chatroom.dto;

import com.api.farmingsoon.domain.chat.model.Chat;
import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.member.model.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomResponse {

    private Long chatRoomId; // ChatRoomDetails 조회 시 사용

    private String toUserName;

    private String toUserProfileImage;

    private String lastMessage;

    private LocalDateTime lastChatTime;

    @Builder
    public ChatRoomResponse(Long chatRoomId, String toUserName, String toUserProfileImage, String lastMessage, LocalDateTime lastChatTime ){
        this.chatRoomId = chatRoomId;
        this.toUserName = toUserName;
        this.toUserProfileImage = toUserProfileImage;
        this.lastMessage = lastMessage;
        this.lastChatTime = lastChatTime;
    }

    public static ChatRoomResponse of(ChatRoom chatRoom, String fromUserEmail) {
        Member toMember =  ChatRoom.resolveToMember(chatRoom, fromUserEmail);
        List<Chat> chatList = chatRoom.getChatList();
        Chat lastChat = chatList.get(chatList.size() - 1);

        return ChatRoomResponse.builder()
                .chatRoomId(chatRoom.getId())
                .toUserName(toMember.getNickname())
                .toUserProfileImage(toMember.getProfileImg())
                .lastMessage(lastChat.getMessage())
                .lastChatTime(lastChat.getCreatedAt())
                .build();
    }
}
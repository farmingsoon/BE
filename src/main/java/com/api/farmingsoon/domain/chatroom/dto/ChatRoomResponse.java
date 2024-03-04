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

    private Long unReadMessageCount;

    @Builder
    public ChatRoomResponse(Long chatRoomId, String toUserName, String toUserProfileImage, String lastMessage, LocalDateTime lastChatTime, Long unReadMessageCount){
        this.chatRoomId = chatRoomId;
        this.toUserName = toUserName;
        this.toUserProfileImage = toUserProfileImage;
        this.lastMessage = lastMessage;
        this.lastChatTime = lastChatTime;
        this.unReadMessageCount = unReadMessageCount;
    }

    public static ChatRoomResponse of(ChatRoom chatRoom, String fromUserEmail) {
        Member toMember =  ChatRoom.resolveToReceiver(chatRoom, fromUserEmail);
        List<Chat> chatList = chatRoom.getChatList();
        Chat lastChat = chatList.get(chatList.size() - 1);
        // 읽지 않았고 sender가 내가 아닌
        Long unReadMessageCount = (long) chatList.stream()
                .filter(chat -> chat.getIsRead() == false && chat.getSender().getEmail() != fromUserEmail)
                .toList().size();

        return ChatRoomResponse.builder()
                .chatRoomId(chatRoom.getId())
                .toUserName(toMember.getNickname())
                .toUserProfileImage(toMember.getProfileImg())
                .lastMessage(lastChat.getMessage())
                .lastChatTime(lastChat.getCreatedAt())
                .unReadMessageCount(unReadMessageCount)
                .build();
    }
}
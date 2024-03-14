package com.api.farmingsoon.domain.chat.repository;

import com.api.farmingsoon.domain.chat.model.Chat;
import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.member.model.Member;

import java.util.List;

public interface ChatRepositoryCustom {
    List<Chat> findMyNotReadChatList(ChatRoom chatroom, Member member);
    void readAllMyNotReadChatList(ChatRoom chatroom, Member member);
}

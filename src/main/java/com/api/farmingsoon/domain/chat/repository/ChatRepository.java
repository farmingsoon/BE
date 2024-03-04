package com.api.farmingsoon.domain.chat.repository;

import com.api.farmingsoon.domain.chat.model.Chat;
import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.member.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long>, ChatRepositoryCustom {
    Page<Chat> findByChatRoomOrderByIdAsc(ChatRoom chatRoom, Pageable pageable);


}

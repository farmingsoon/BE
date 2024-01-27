package com.api.farmingsoon.domain.chatroom.repository;

import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findChatRoomByBuyerAndItem(Member buyer, Item item);

    List<ChatRoom> findChatRoomByBuyerOrSeller(Member member, Member member1);
}

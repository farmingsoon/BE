package com.api.farmingsoon.domain.chat.repository;

import com.api.farmingsoon.domain.chat.model.Chat;
import com.api.farmingsoon.domain.chat.model.QChat;
import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.member.model.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.api.farmingsoon.domain.bid.model.QBid.bid;
import static com.api.farmingsoon.domain.chat.model.QChat.*;
import static com.api.farmingsoon.domain.item.domain.QItem.item;
import static com.api.farmingsoon.domain.member.model.QMember.member;


@Slf4j
@RequiredArgsConstructor
public class ChatRepositoryCustomImpl implements ChatRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public List<Chat> findMyNotReadChatList(ChatRoom chatRoom, Member member) {
        return  queryFactory.selectFrom(chat)
                        .where(chat.chatRoom.eq(chatRoom), chat.isRead.isFalse(), chat.sender.ne(member))
                        .fetch();

    }

    @Override
    public void readAllMyNotReadChatList(ChatRoom chatroom, Member member) {
        queryFactory.update(chat)
                .where(chat.chatRoom.eq(chatroom), chat.isRead.isFalse(), chat.sender.ne(member))
                .set(chat.isRead, true)
                .execute();
        em.flush();
        em.clear();

    }
}

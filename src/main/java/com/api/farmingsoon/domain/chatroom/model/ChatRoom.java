package com.api.farmingsoon.domain.chatroom.model;

import com.api.farmingsoon.common.auditing.BaseTimeEntity;
import com.api.farmingsoon.domain.chat.model.Chat;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.REMOVE)
    private List<Chat> chatList = new ArrayList<>();

    @Builder
    private ChatRoom(Member seller, Member buyer, Item item) {
        this.seller = seller;
        this.buyer = buyer;
        this.item = item;
    }

    public static ChatRoom of(Member seller, Member buyer, Item item) {
        return ChatRoom.builder()
                .item(item)
                .seller(seller)
                .buyer(buyer)
                .build();
    }


    /**
     * @Description
     * fromUser - 채팅을 거는 사람이
     * 판매자라면 상대방은 구매자
     * 반대라면 상대방은 판매자
     */
    public static Member resolveToMember(ChatRoom chatRoom, String fromUserEmail) {
        return chatRoom.seller.getEmail().equals(fromUserEmail) ?
                chatRoom.buyer :
                chatRoom.seller;
    }
}

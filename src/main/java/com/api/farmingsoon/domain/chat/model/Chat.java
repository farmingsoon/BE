package com.api.farmingsoon.domain.chat.model;

import com.api.farmingsoon.common.auditing.BaseTimeEntity;
import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ChatRoom chatRoom;

    @Column(length = 500)
    private String message;

    @Column
    private Boolean isRead;

    @Builder
    public Chat(Member sender, ChatRoom chatRoom, String message, Boolean isRead) {
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.message = message;
        this.isRead = isRead;
    }

    public void read() {
        this.isRead = true;
    }
}
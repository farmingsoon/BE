package com.api.farmingsoon.domain.notification.model;

import com.api.farmingsoon.common.auditing.BaseTimeEntity;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.member.model.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member receiver;

    private Long itemId;

    private LocalDateTime readDate;

    private String message;
    @Builder
    private Notification(Member receiver, String message, Long itemId) {
        this.receiver = receiver;
        this.itemId = itemId;
        this.message = message;
    }

    public static Notification of(Member receiver, String message, Long itemId)
    {
        return Notification.builder()
                .receiver(receiver)
                .message(message)
                .itemId(itemId)
                .build();
    }

    public void read() {
        this.readDate = LocalDateTime.now();
    }
}

package com.api.farmingsoon.domain.notification.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class NotReadChatEvent {

    private Long receiverId;

    public NotReadChatEvent(Long receiverId) {
        this.receiverId = receiverId;
    }
}

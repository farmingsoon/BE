package com.api.farmingsoon.domain.notification.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChatNotificationDebounceKeyExpiredEvent {
    private Long receiverId;
    public ChatNotificationDebounceKeyExpiredEvent(Long receiverId) {
        this.receiverId = receiverId;
    }
}

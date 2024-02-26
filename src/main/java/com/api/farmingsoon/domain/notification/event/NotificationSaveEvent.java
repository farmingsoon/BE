package com.api.farmingsoon.domain.notification.event;

import com.api.farmingsoon.domain.item.domain.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class NotificationSaveEvent {
    private List<Long> senderIdList;
    private String message;

    @Builder
    private NotificationSaveEvent(List<Long> senderIdList, String message) {
        this.senderIdList = senderIdList;
        this.message = message;
    }

    public static NotificationSaveEvent of(List<Long> senderIdList, String message)
    {
        return NotificationSaveEvent.builder()
                .senderIdList(senderIdList)
                .message(message)
                .build();
    }
}

package com.api.farmingsoon.domain.notification.dto;


import com.api.farmingsoon.domain.notification.model.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationResponse {
    private Long itemId;
    private Long notificationId;
    private String message;

    @Builder
    private NotificationResponse(Long itemId, Long notificationId, String message) {
        this.itemId = itemId;
        this.notificationId = notificationId;
        this.message = message;
    }

    public static NotificationResponse of(Notification notification){
        return NotificationResponse.builder()
                .itemId(notification.getItemId())
                .notificationId(notification.getId())
                .message(notification.getMessage()).build();
    }
}

package com.api.farmingsoon.domain.notification.dto;

import com.api.farmingsoon.common.pagenation.Pagination;
import com.api.farmingsoon.domain.notification.model.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor
@Getter
public class NotificationListResponse {

    private Pagination<NotificationResponse> pagination;
    private List<NotificationResponse> notifications;

    @Builder
    private NotificationListResponse(Pagination<NotificationResponse> pagination, List<NotificationResponse> notifications) {
        this.pagination = pagination;
        this.notifications = notifications;
    }

    public static NotificationListResponse of(Page<Notification> notificationPage)
    {
        Page<NotificationResponse> notificationResponsePage = notificationPage.map(NotificationResponse::of);
        return NotificationListResponse.builder()
                .notifications(notificationResponsePage.getContent())
                .pagination(Pagination.of(notificationPage)).build();
    }
}

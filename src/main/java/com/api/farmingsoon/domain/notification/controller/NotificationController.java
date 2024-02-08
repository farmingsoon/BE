package com.api.farmingsoon.domain.notification.controller;

import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.domain.notification.dto.NotificationListResponse;
import com.api.farmingsoon.domain.notification.dto.NotificationResponse;
import com.api.farmingsoon.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    //@LoginChecking - 테스트를 위해 비활성화
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe() {
        return notificationService.subscribe();
    }
    @PatchMapping("/{notificationId}")
    public Response<Void> readNotification(@PathVariable("notificationId") Long notificationId) {
        notificationService.readNotification(notificationId);
        return Response.success(HttpStatus.OK, "알림을 읽었습니다");
    }
    @PatchMapping
    public Response<Void> readAllNotification() {
        notificationService.readAllNotification();
        return Response.success(HttpStatus.OK, "모든 알림을 읽음 처리했습니다");
    }

    @GetMapping("/me")
    public Response<NotificationListResponse> getMyNotifications(
            @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        NotificationListResponse myNotifications = notificationService.getMyNotifications(pageable);
        return Response.success(HttpStatus.OK, "알림 조회 성공", myNotifications);
    }
    /**
     * @Description 테스트 용도

    @PostMapping("/send-data/{id}")
    public void sendData(@PathVariable(name = "id") Long id) {
        notificationService.saveAndSend(id, "success");
    }
     */
}

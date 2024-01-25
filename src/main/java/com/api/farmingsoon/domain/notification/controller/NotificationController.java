package com.api.farmingsoon.domain.notification.controller;

import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.domain.notification.dto.NotificationResponse;
import com.api.farmingsoon.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<Void> readNotification(@PathVariable("notificationId") Long notificationId) {
        notificationService.readNotification(notificationId);
        return ResponseEntity.ok().build();
    }
    @PatchMapping
    public ResponseEntity<Void> readAllNotification() {
        notificationService.readAllNotification();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public Response<List<NotificationResponse>> getMyNotifications() {
        List<NotificationResponse> myNotifications = notificationService.getMyNotifications();
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

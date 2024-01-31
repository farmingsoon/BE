package com.api.farmingsoon.domain.notification.service;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.common.sse.SseService;
import com.api.farmingsoon.common.util.AuthenticationUtils;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.notification.dto.NotificationResponse;
import com.api.farmingsoon.domain.notification.model.Notification;
import com.api.farmingsoon.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseService sseService;
    private final AuthenticationUtils authenticationUtils;

    public SseEmitter subscribe() {
        //return sseService.subscribe(authenticationUtils.getAuthenticationMember().getId());
        return sseService.subscribe(1L); // 테스트 전용
    }

    public List<NotificationResponse> getMyNotifications() {
        List<Notification> notifications = notificationRepository.findByReceiverAndReadDateIsNull(authenticationUtils.getAuthenticationMember());
        return notifications.stream().map(NotificationResponse::of).toList();
    }
    @Transactional
    public void readNotification(Long notificationId){
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_NOTIFICATION));
        notification.read();
    }

    @Transactional
    public void readAllNotification() {
        List<Notification> notifications = notificationRepository.findByReceiverAndReadDateIsNull(authenticationUtils.getAuthenticationMember());
        notifications.forEach(Notification::read);
    }

    /**
     @Description
     알림저장
     알림전송(비동기 처리 예정)
     **/

    // 구매자와 판매자에게 입찰이 등록되었다고 알리기
    @Transactional
    public void createAndSendNewBidNotification(Item item) {

        Member seller = item.getMember();
        List<Member> list = item.getBidList().stream().map(Bid::getMember).toList();
        list.add(seller);

        // @Todo 이 부분 비동기 처리하면서 리팩토링 하겠습니다.
        list.stream().map(receiver -> notificationRepository.save(Notification.of(receiver,"새로운 입찰이 등록되었습니다.", item.getId())));
        list.stream().forEach(receiver -> sseService.sendToClient(receiver.getId(), "새로운 입찰이 등록되었습니다."));

    }
    @Transactional
    public void createAndSendSoldOutNotification(Item item) {
        /**
         @Todo 구매자에게 입찰이 등록되었다고 알리기
         알림저장
         알림전송(비동기 처리 예정)
         **/

    }
    @Transactional
    public void createAndSendBidEndNotification(Item item) {
        /**
         @Todo 구매자와 판매자에게 입찰이 등록되었다고 알리기
         알림저장
         알림전송(비동기 처리 예정)
         **/

    }


}

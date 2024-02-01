package com.api.farmingsoon.domain.notification.service;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.common.sse.SseService;
import com.api.farmingsoon.common.util.AuthenticationUtils;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.service.ItemService;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.notification.dto.NotificationResponse;
import com.api.farmingsoon.domain.notification.model.Notification;
import com.api.farmingsoon.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
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
     알림전송
     @Todo (비동기 처리 예정)
     sse로 보내는 알림은 메시지 대신 알림 타입만 보내주면될듯(Chat or Notification)
     **/

    // 구매자와 판매자에게 입찰이 등록되었다고 알리기
    public void createAndSendNewBidNotification(Item item) {
        List<Member> receiverList = new ArrayList<>(item.getBidList().stream().map(Bid::getMember).toList()); // 입찰자들
        receiverList.add(item.getMember()); // 판매자 추가

        receiverList.forEach(receiver -> notificationRepository.save(Notification.of(receiver,"새로운 입찰이 등록되었습니다.", item.getId())));
        receiverList.forEach(receiver -> sseService.sendToClient(receiver.getId(), "새로운 입찰이 등록되었습니다."));

    }
    public void createAndSendSoldOutNotification(List<Member> bidderList, Item item) {

        notificationRepository.save(Notification.of(bidderList.get(0),"입찰하신 상품에 낙찰되셨습니다", item.getId()));
        bidderList.stream().skip(1).map(receiver -> notificationRepository.save(Notification.of(receiver,"입찰하신 상품에 낙찰받지 못하셨습니다.", item.getId())));

        bidderList.stream().forEach(receiver -> sseService.sendToClient(receiver.getId(), "알림"));
    }

    public void createAndSendBidEndNotification(Item item) {
        /**
         @Todo 구매자와 판매자에게 입찰이 등록되었다고 알리기
         알림저장
         알림전송(비동기 처리 예정)
         **/

    }




}

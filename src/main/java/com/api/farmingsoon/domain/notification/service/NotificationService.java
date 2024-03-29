package com.api.farmingsoon.domain.notification.service;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.common.sse.SseService;
import com.api.farmingsoon.common.util.AuthenticationUtils;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.notification.dto.NotificationListResponse;
import com.api.farmingsoon.domain.notification.dto.NotificationResponse;
import com.api.farmingsoon.domain.notification.event.NotificationSaveEvent;
import com.api.farmingsoon.domain.notification.model.Notification;
import com.api.farmingsoon.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseService sseService;
    private final AuthenticationUtils authenticationUtils;
    private final ApplicationEventPublisher eventPublisher;

    public SseEmitter subscribe() {
        return sseService.subscribe(authenticationUtils.getAuthenticationMember().getId());
    }

    @Transactional(readOnly = true)
    public NotificationListResponse getMyNotifications(Pageable pageable) {
        Page<Notification> notifications = notificationRepository.findByReceiverAndReadDateIsNull(authenticationUtils.getAuthenticationMember(), pageable);

        return NotificationListResponse.of(notifications);
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

    @Transactional
    // 현재 입찰을 등록하는 회원을 제외한 입찰자, 판매자에게 입찰이 등록되었다고 알리기
    public void createAndSendNewBidNotification(Item item) {
        Member bidder = authenticationUtils.getAuthenticationMember();
        List<Member> receiverList = new ArrayList<>(item.getBidList().stream().map(Bid::getMember).filter(member -> bidder != member).toList()); // 입찰자들
        receiverList.add(item.getMember()); // 판매자 추가

        receiverList.forEach(receiver -> notificationRepository.save(Notification.of(receiver,"새로운 입찰이 등록되었습니다.", item.getId())));

        eventPublisher.publishEvent(NotificationSaveEvent.of(receiverList.stream().map(Member::getId).toList(), "새로운 입찰이 등록되었습니다."));
        //receiverList.forEach(receiver -> sseService.sendToClient(receiver.getId(), "새로운 입찰이 등록되었습니다."));

    }
    // 입찰자들에 대해 먼저 처리한 후 List에 판매자를 추가하여 알림 전송에 재활용
    @Transactional
    public void createAndSendBidEndNotification(Item item) {
        List<Member> receiverList = new ArrayList<>(item.getBidList().stream().map(Bid::getMember).toList()); // 입찰자들
        receiverList.forEach(bidder -> notificationRepository.save(Notification.of(bidder, "입찰이 종료되었습니다. 거래를 기다려주세요", item.getId())));

        Member seller = item.getMember();
        notificationRepository.save(Notification.of(seller, "입찰이 종료되었습니다. 거래를 진행해주세요", item.getId()));
        receiverList.add(seller); // 판매자 추가

        eventPublisher.publishEvent(NotificationSaveEvent.of(receiverList.stream().map(Member::getId).toList(), "입찰이 종료되었습니다."));

        //receiverList.forEach(receiver -> sseService.sendToClient(receiver.getId(), "입찰이 종료되었습니다"));

    }
    @Transactional
    public void createAndSendSoldOutNotification(List<Member> bidderList, Item item) {
        if(!bidderList.isEmpty()) {
            notificationRepository.save(Notification.of(bidderList.get(0), "입찰하신 상품에 낙찰되셨습니다.", item.getId()));
            bidderList.stream().skip(1).forEach(receiver -> notificationRepository.save(Notification.of(receiver, "입찰하신 상품에 낙찰받지 못하셨습니다.", item.getId())));

            eventPublisher.publishEvent(NotificationSaveEvent.of(bidderList.stream().map(Member::getId).toList(), "입찰 등록한 상품이 판매 완료되었습니다."));
        }
        //bidderList.forEach(receiver -> sseService.sendToClient(receiver.getId(), "입찰 등록한 상품이 판매 완료되었습니다."));
    }






}

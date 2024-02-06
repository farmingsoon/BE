package com.api.farmingsoon.domain.notification.repository;

import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.notification.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByReceiverAndReadDateIsNull(Member member, Pageable pageable);

    List<Notification> findByReceiverAndReadDateIsNull(Member member);
}

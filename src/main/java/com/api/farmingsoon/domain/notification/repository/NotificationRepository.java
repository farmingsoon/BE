package com.api.farmingsoon.domain.notification.repository;

import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserAndReadDateIsNull(Member member);
}

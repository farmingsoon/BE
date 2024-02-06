package com.api.farmingsoon.domain.like.repository;

import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.like.model.LikeableItem;
import com.api.farmingsoon.domain.member.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeableItemRepository extends JpaRepository<LikeableItem, Long> {

    Optional<LikeableItem> findByMemberAndItem(Member member, Item item);

    Page<LikeableItem> findAllByMember(Member member, Pageable pageable);
}

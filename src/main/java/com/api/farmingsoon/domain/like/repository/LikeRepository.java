package com.api.farmingsoon.domain.like.repository;

import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.like.model.Like;
import com.api.farmingsoon.domain.member.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByMemberAndItem(Member member, Item item);

    List<Like> findAllByMember(Member member);

    @Query("select count(*) from Like l where l.item = :item")
    long countByItem(@Param("item") Item item);
}

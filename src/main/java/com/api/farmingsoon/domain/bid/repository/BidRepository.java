package com.api.farmingsoon.domain.bid.repository;

import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.member.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {

    Page<Bid> findAllByMember(Member member, Pageable pageable);

    void deleteAllByMember(Member member);

    Page<Bid> findAllByItemId(Long itemId);
}

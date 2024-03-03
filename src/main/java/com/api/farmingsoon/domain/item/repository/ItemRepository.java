package com.api.farmingsoon.domain.item.repository;

import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.member.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    Page<Item> findAllByMember(Member member, Pageable pageable);
}

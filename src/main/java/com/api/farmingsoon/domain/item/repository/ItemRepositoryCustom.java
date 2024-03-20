package com.api.farmingsoon.domain.item.repository;

import com.api.farmingsoon.domain.item.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {

    Page<Item> findItemList(String category, String title, Pageable pageable, String sortCode, String itemStatus);

    List<Item> findNotEndBidItemList();

    List<Item> findBiddingItemList();
}

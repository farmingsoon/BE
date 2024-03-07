package com.api.farmingsoon.domain.item.repository;

import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.dto.ItemResponseBySubQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepositoryCustom {

    Page<Item> findItemList(String category, String title, Pageable pageable, String sortcode);

    List<Item> findNotEndBidItemList();

    List<Item> findBiddingItemList();

    Page<ItemResponseBySubQuery> findItemResponseList(String category, String keyword, Pageable pageable, String sortcode);
}

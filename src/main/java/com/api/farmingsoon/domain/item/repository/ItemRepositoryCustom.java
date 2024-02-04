package com.api.farmingsoon.domain.item.repository;

import com.api.farmingsoon.domain.item.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    Page<Item> findItemList(String category, String title, Pageable pageable, String sortcode);

}

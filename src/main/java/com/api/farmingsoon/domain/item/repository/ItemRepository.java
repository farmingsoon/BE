package com.api.farmingsoon.domain.item.repository;

import com.api.farmingsoon.domain.item.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}

package com.api.farmingsoon.common.event;

import com.api.farmingsoon.domain.item.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class ItemSoldOutEvent {
    private Item item;
}

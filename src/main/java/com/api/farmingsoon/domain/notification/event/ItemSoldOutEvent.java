package com.api.farmingsoon.domain.notification.event;

import com.api.farmingsoon.domain.item.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class ItemSoldOutEvent {
    private Long itemId;
    private Long buyerId;
}

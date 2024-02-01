package com.api.farmingsoon.domain.notification.event;

import com.api.farmingsoon.domain.item.domain.Item;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BidEndEvent {
    private Item item;
}

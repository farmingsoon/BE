package com.api.farmingsoon.domain.notification.event;

import com.api.farmingsoon.domain.item.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
public class BidRegisterEvent {
    private Long itemId;
}
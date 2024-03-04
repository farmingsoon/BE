package com.api.farmingsoon.domain.item.event;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BidEndKeyExpiredEvent {
    private Long itemId;

    public BidEndKeyExpiredEvent(Long itemId) {
        this.itemId = itemId;
    }
}

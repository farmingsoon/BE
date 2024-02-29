package com.api.farmingsoon.domain.item.event;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BidEndEvent {
    private Long itemId;

    public BidEndEvent(Long itemId) {
        this.itemId = itemId;
    }
}

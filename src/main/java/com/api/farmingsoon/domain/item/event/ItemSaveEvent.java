package com.api.farmingsoon.domain.item.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ItemSaveEvent {

    private Long itemId;
    private Integer period;

    public ItemSaveEvent(Long itemId, Integer period) {
        this.itemId = itemId;
        this.period = period;
    }
}

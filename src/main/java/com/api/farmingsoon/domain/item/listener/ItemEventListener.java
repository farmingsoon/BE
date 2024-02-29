package com.api.farmingsoon.domain.item.listener;

import com.api.farmingsoon.domain.item.event.BidEndEvent;
import com.api.farmingsoon.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ItemEventListener {

    private final ItemService itemService;

    @EventListener
    public void bidEnd(BidEndEvent event){
        itemService.bidEnd(event.getItemId());
    }
}

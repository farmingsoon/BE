package com.api.farmingsoon.domain.item.listener;

import com.api.farmingsoon.domain.item.event.BidEndKeyExpiredEvent;
import com.api.farmingsoon.domain.item.event.BidEndSchedulerRunEvent;
import com.api.farmingsoon.domain.item.event.ItemSaveEvent;
import com.api.farmingsoon.domain.item.service.ItemRedisService;
import com.api.farmingsoon.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
@Slf4j
public class ItemEventListener {

    private final ItemService itemService;
    private final ItemRedisService itemRedisService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void setBid(ItemSaveEvent event){
        itemRedisService.setBidEndTime(event.getItemId(), event.getPeriod());
    }
    @EventListener
    public void bidEnd(BidEndKeyExpiredEvent event){
          itemService.bidEnd(event.getItemId());
    }

    @EventListener
    public void bidEnd(BidEndSchedulerRunEvent event){
        itemService.bidEndByScheduler();
    }
}

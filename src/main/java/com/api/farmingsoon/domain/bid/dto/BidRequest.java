package com.api.farmingsoon.domain.bid.dto;

import com.api.farmingsoon.domain.bid.model.Bid;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BidRequest {


    private Long itemId;
    private int price;
    @Builder
    private BidRequest(Long itemId, int price) {
        this.itemId = itemId;
        this.price = price;
    }

}

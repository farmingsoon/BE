package com.api.farmingsoon.domain.bid.dto;

import com.api.farmingsoon.domain.bid.model.Bid;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BidsResponse {

    private Long itemId;
    private String itemName;
    private int price;

    public static BidsResponse toDto(Bid bid) {
        return BidsResponse.builder()
                .itemId(bid.getItem().getId())
                .itemName(bid.getItem().getTitle())
                .price(bid.getPrice())
                .build();
    }

}

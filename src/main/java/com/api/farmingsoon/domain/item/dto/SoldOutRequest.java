package com.api.farmingsoon.domain.item.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoldOutRequest {
    private Long buyerId;
    private Integer awardPrice; // 낙찰가

    @Builder
    private SoldOutRequest(Long buyerId, Integer awardPrice) {
        this.buyerId = buyerId;
        this.awardPrice = awardPrice;
    }
}

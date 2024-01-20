package com.api.farmingsoon.domain.item.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ItemStatus {
    BIDDING("경매중"),
    SOLDOUT("판매완료"),
    BID_END("경매종료");

    private final String status;

}

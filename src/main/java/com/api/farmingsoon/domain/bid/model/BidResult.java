package com.api.farmingsoon.domain.bid.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BidResult {
    BIDDING("경매중"),
    BID_SUCCESS("판매완료"),
    BID_FAIL("경매종료");

    private final String status;
}

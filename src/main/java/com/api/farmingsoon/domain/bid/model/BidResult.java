package com.api.farmingsoon.domain.bid.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BidResult {
    BIDDING("경매중"),
    BID_SUCCESS("입찰 성공"),
    BID_FAIL("입찰 실패");

    private final String status;
}

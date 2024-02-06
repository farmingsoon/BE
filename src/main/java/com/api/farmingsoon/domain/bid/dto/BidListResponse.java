package com.api.farmingsoon.domain.bid.dto;

import com.api.farmingsoon.common.pagenation.Pagination;
import com.api.farmingsoon.domain.bid.model.Bid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BidListResponse {

    private List<BidResponse> bids;
    private Pagination<BidResponse> pagination;

    @Builder
    private BidListResponse(List<BidResponse> bids, Pagination<BidResponse> pagination) {
        this.bids = bids;
        this.pagination = pagination;
    }

    public static BidListResponse of(Page<Bid> bidPage) {
        Page<BidResponse> bidDtoPage = bidPage.map(BidResponse::of);
        return BidListResponse.builder()
                .bids(bidDtoPage.getContent())
                .pagination(Pagination.of(bidDtoPage))
                .build();
    }

    @Getter
    @Builder
    private static class BidResponse {

        private Long bidderId;
        private Long itemId;
        private String itemName;
        private int price;

        private static BidResponse of(Bid bid) {
            return BidResponse.builder()
                    .bidderId(bid.getMember().getId())
                    .itemId(bid.getItem().getId())
                    .itemName(bid.getItem().getTitle())
                    .price(bid.getPrice())
                    .build();
        }
    }
}

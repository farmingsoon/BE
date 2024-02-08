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
    @NoArgsConstructor
    private static class BidResponse {

        private Long bidderId;
        private Long bidId;
        private Long itemId;
        private String bidderName;
        private String bidderProfileUrl;
        private int price;

        @Builder
        private BidResponse(Long bidderId,Long bidId, Long itemId, String bidderName, String bidderProfileUrl, int price) {
            this.bidId = bidId;
            this.bidderId = bidderId;
            this.bidderName = bidderName;
            this.bidderProfileUrl = bidderProfileUrl;
            this.itemId = itemId;
            this.price = price;
        }

        public static BidResponse of(Bid bid) {
            return BidResponse.builder()
                    .bidderId(bid.getMember().getId())
                    .bidId(bid.getId())
                    .itemId(bid.getItem().getId())
                    .bidderName(bid.getMember().getNickname())
                    .bidderProfileUrl(bid.getMember().getProfileImg())
                    .price(bid.getPrice())
                    .build();
        }
    }
}

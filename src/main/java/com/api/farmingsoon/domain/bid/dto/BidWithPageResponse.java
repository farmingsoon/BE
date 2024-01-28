package com.api.farmingsoon.domain.bid.dto;

import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.item.domain.Item;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class BidWithPageResponse {

    private List<BidDto> items;
    private Pagination pagination;

    public static BidWithPageResponse of(Page<Bid> bidPage) {
        Page<BidDto> bidDtoPage = bidPage.map(BidDto::of);
        return BidWithPageResponse.builder()
                .items(bidDtoPage.getContent())
                .pagination(Pagination.of(bidDtoPage))
                .build();
    }

    @Getter
    @Builder
    private static class BidDto {

        private Long itemId;
        private String itemName;
        private int price;

        private static BidDto of(Bid bid) {
            return BidDto.builder()
                    .itemId(bid.getItem().getId())
                    .itemName(bid.getItem().getTitle())
                    .price(bid.getPrice())
                    .build();
        }
    }

    @Getter
    @Builder
    private static class Pagination {

        private int totalPages; // 전체 페이지수
        private long totalElements; // 전체 개수
        private int page; // 현재 페이지
        private boolean hasNext; // 다음 페이지 존재 여부
        private boolean hasPrevious; // 이전 페이지 존재 여부
        private int requestSize;
        private int itemSize;

        private static Pagination of(Page<BidDto> itemDtoPage) {
            return builder()
                    .totalPages(itemDtoPage.getTotalPages())
                    .totalElements(itemDtoPage.getTotalElements())
                    .page(itemDtoPage.getNumber() + 1)
                    .hasNext(itemDtoPage.hasNext())
                    .hasPrevious(itemDtoPage.hasPrevious())
                    .requestSize(itemDtoPage.getSize())
                    .itemSize(itemDtoPage.getNumberOfElements())
                    .build();
        }
    }
}

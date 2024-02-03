package com.api.farmingsoon.domain.item.dto;

import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.item.domain.Item;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ItemListResponse {

    private List<ItemResponse> items; // 상품 데이터
    private Pagination pagination; // 페이지 관련 데이터

    public static ItemListResponse of(Page<Item> itemPage) {
        Page<ItemResponse> itemDtoPage = itemPage.map(ItemResponse::of); // Page<Item> -> Page<ItemDto>
        return ItemListResponse.builder()
                .items(itemDtoPage.getContent())
                .pagination(Pagination.of(itemDtoPage))
                .build();
    }

    @Getter
    @Builder
    private static class ItemResponse {

        private Long itemId; // 상품 접근
        private String title;
        private String description;
        private LocalDateTime expiredAt;
        private Integer highestPrice;
        private Integer hopePrice;
        private Integer lowestPrice;
        private String itemStatus;
        private Integer bidSize;
        // @Todo 좋아요 수 추가

        private static ItemResponse of(Item item) {
            return ItemResponse.builder()
                    .itemId(item.getId())
                    .title(item.getTitle())
                    .description(item.getDescription())
                    .hopePrice(item.getHopePrice())
                    .highestPrice(item.getBidList().stream().map(Bid::getPrice).max(Integer::compareTo).orElse(null))
                    .lowestPrice(item.getBidList().stream().map(Bid::getPrice).min(Integer::compareTo).orElse(null))
                    .expiredAt(item.getExpiredAt())
                    .itemStatus(item.getItemStatus().getStatus())
                    .bidSize(item.getBidList().size())
                    .build();
        }
    }

    @Getter
    @Builder
    private static class Pagination {

        private int totalPages; // 전체 페이지수
        private long totalElements; // 전체 개수
        private int page; // 현재 페이지(1부터 시작)
        private boolean hasNext; // 다음 페이지 존재 여부
        private boolean hasPrevious; // 이전 페이지 존재 여부
        private int pageSize; // 현재 페이지의 전체 사이즈
        private int elementSize; // 현재 페이지에 있는 요소의 수

        private static Pagination of(Page<ItemResponse> itemDtoPage) {
            return builder()
                    .totalPages(itemDtoPage.getTotalPages())
                    .totalElements(itemDtoPage.getTotalElements())
                    .page(itemDtoPage.getNumber() + 1)
                    .hasNext(itemDtoPage.hasNext())
                    .hasPrevious(itemDtoPage.hasPrevious())
                    .pageSize(itemDtoPage.getSize())
                    .elementSize(itemDtoPage.getNumberOfElements())
                    .build();
        }
    }
}

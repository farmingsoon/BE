package com.api.farmingsoon.domain.item.dto;

import com.api.farmingsoon.common.pagenation.Pagination;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.item.domain.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class MyItemListResponse {
    private List<MyItemResponse> items; // 상품 데이터
    private Pagination<MyItemResponse> pagination; // 페이지 관련 데이터

    @Builder
    public MyItemListResponse(List<MyItemResponse> items, Pagination<MyItemResponse> pagination) {
        this.items = items;
        this.pagination = pagination;
    }

    public static MyItemListResponse of(Page<Item> itemPage) {
        Page<MyItemResponse> myItemResponsePage = itemPage.map(item -> MyItemResponse.of(item));// Page<Item> -> Page<ItemDto>

        return MyItemListResponse.builder()
                .items(myItemResponsePage.getContent())
                .pagination(Pagination.of(myItemResponsePage))
                .build();
    }

    @Getter
    @NoArgsConstructor
    public static class MyItemResponse {

        private Long itemId; // 상품 접근
        private String title;
        private String description;
        private LocalDateTime expiredAt;
        private Integer hopePrice;
        private Integer highestPrice;
        private Integer awardPrice;
        private String itemStatus;
        private Integer bidCount;
        private Integer likeCount;
        private Integer viewCount;
        private String thumbnailImgUrl;
        @Builder
        private MyItemResponse(Long itemId, String title, String description, LocalDateTime expiredAt, Integer hopePrice,Integer awardPrice, Integer highestPrice, String itemStatus, Integer bidCount, Integer likeCount, Integer viewCount, String thumbnailImgUrl) {
            this.itemId = itemId;
            this.title = title;
            this.description = description;
            this.expiredAt = expiredAt;
            this.hopePrice = hopePrice;
            this.highestPrice = highestPrice;
            this.awardPrice = awardPrice;
            this.itemStatus = itemStatus;
            this.bidCount = bidCount;
            this.likeCount = likeCount;
            this.viewCount = viewCount;
            this.thumbnailImgUrl = thumbnailImgUrl;
        }

        private static MyItemResponse of(Item item) {

            return MyItemResponse.builder()
                    .itemId(item.getId())
                    .title(item.getTitle())
                    .description(item.getDescription())
                    .hopePrice(item.getHopePrice())
                    .awardPrice(item.getAwardPrice())
                    .highestPrice(item.getBidList().stream().map(Bid::getPrice).max(Integer::compareTo).orElse(null))
                    .expiredAt(item.getExpiredAt())
                    .itemStatus(item.getItemStatus().getStatus())
                    .bidCount(item.getBidList().size())
                    .viewCount(item.getViewCount())
                    .likeCount(item.getLikeableItemList().size())
                    .thumbnailImgUrl(item.getThumbnailImageUrl())
                    .build();
        }
    }


}

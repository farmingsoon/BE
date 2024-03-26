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
public class LikeableItemListResponse {
    private List<LikeableItemResponse> items; // 상품 데이터
    private Pagination<LikeableItemResponse> pagination; // 페이지 관련 데이터

    @Builder
    public LikeableItemListResponse(List<LikeableItemResponse> items, Pagination<LikeableItemResponse> pagination) {
        this.items = items;
        this.pagination = pagination;
    }

    public static LikeableItemListResponse of(Page<Item> itemPage) {
        Page<LikeableItemResponse> myItemResponsePage = itemPage.map(item -> LikeableItemResponse.of(item));// Page<Item> -> Page<ItemDto>

        return LikeableItemListResponse.builder()
                .items(myItemResponsePage.getContent())
                .pagination(Pagination.of(myItemResponsePage))
                .build();
    }

    @Getter
    @NoArgsConstructor
    public static class LikeableItemResponse {

        private Long itemId; // 상품 접근
        private String title;
        private String description;
        private LocalDateTime expiredAt;
        private Integer hopePrice;
        private String itemStatus;
        private Integer bidCount;
        private Integer likeCount;
        private Integer viewCount;
        private Integer awardPrice;
        private Integer highestPrice;
        private String thumbnailImgUrl;
        @Builder
        private LikeableItemResponse(Long itemId, String title, String description, LocalDateTime expiredAt,Integer awardPrice, Integer hopePrice, Integer highestPrice, String itemStatus, Integer bidCount, Integer likeCount, Integer viewCount, String thumbnailImgUrl, Boolean likeStatus) {
            this.itemId = itemId;
            this.title = title;
            this.description = description;
            this.expiredAt = expiredAt;
            this.hopePrice = hopePrice;
            this.itemStatus = itemStatus;
            this.bidCount = bidCount;
            this.likeCount = likeCount;
            this.viewCount = viewCount;
            this.highestPrice = highestPrice;
            this.awardPrice = awardPrice;
            this.thumbnailImgUrl = thumbnailImgUrl;
        }

        private static LikeableItemResponse of(Item item) {

            return LikeableItemResponse.builder()
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

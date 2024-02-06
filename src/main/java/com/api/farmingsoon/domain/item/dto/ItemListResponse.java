package com.api.farmingsoon.domain.item.dto;

import com.api.farmingsoon.common.pagenation.Pagination;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.like.model.LikeableItem;
import com.api.farmingsoon.domain.member.model.Member;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ItemListResponse {

    private List<ItemResponse> items; // 상품 데이터
    private Pagination<ItemResponse> pagination; // 페이지 관련 데이터

    @Builder
    public ItemListResponse(List<ItemResponse> items, Pagination<ItemResponse> pagination) {
        this.items = items;
        this.pagination = pagination;
    }

    public static ItemListResponse of(Page<Item> itemPage, Member viewer) {
        Page<ItemResponse> itemDtoPage = itemPage.map(item -> ItemResponse.of(item, viewer)); // Page<Item> -> Page<ItemDto>
        return ItemListResponse.builder()
                .items(itemDtoPage.getContent())
                .pagination(Pagination.of(itemDtoPage))
                .build();
    }

    @Getter
    @NoArgsConstructor
    public static class ItemResponse {

        private Long itemId; // 상품 접근
        private String title;
        private String description;
        private LocalDateTime expiredAt;
        private Integer highestPrice;
        private Integer hopePrice;
        private Integer lowestPrice;
        private String itemStatus;
        private Integer bidCount;
        private Integer likeCount;
        private Integer viewCount;
        private String thumbnailImgUrl;
        private Boolean likeStatus;
        @Builder
        private ItemResponse(Long itemId, String title, String description, LocalDateTime expiredAt, Integer highestPrice, Integer hopePrice, Integer lowestPrice, String itemStatus, Integer bidCount, Integer likeCount, Integer viewCount, String thumbnailImgUrl, Boolean likeStatus) {
            this.itemId = itemId;
            this.title = title;
            this.description = description;
            this.expiredAt = expiredAt;
            this.highestPrice = highestPrice;
            this.hopePrice = hopePrice;
            this.lowestPrice = lowestPrice;
            this.itemStatus = itemStatus;
            this.bidCount = bidCount;
            this.likeCount = likeCount;
            this.viewCount = viewCount;
            this.thumbnailImgUrl = thumbnailImgUrl;
            this.likeStatus = likeStatus;
        }

        private static ItemResponse of(Item item, Member viewer) {

            return ItemResponse.builder()
                    .itemId(item.getId())
                    .title(item.getTitle())
                    .description(item.getDescription())
                    .hopePrice(item.getHopePrice())
                    .highestPrice(item.getBidList().stream().map(Bid::getPrice).max(Integer::compareTo).orElse(null))
                    .lowestPrice(item.getBidList().stream().map(Bid::getPrice).min(Integer::compareTo).orElse(null))
                    .expiredAt(item.getExpiredAt())
                    .itemStatus(item.getItemStatus().getStatus())
                    .bidCount(item.getBidList().size())
                    .viewCount(item.getViewCount())
                    .likeCount(item.getLikeableItemList().size())
                    .thumbnailImgUrl(item.getThumbnailImageUrl())
                    .likeStatus(item.getLikeableItemList().stream().map(LikeableItem::getMember).toList().contains(viewer))
                    .build();
        }
    }


}

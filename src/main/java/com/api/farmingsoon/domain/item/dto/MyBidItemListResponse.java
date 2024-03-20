package com.api.farmingsoon.domain.item.dto;

import com.api.farmingsoon.common.pagenation.Pagination;
import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.like.model.LikeableItem;
import com.api.farmingsoon.domain.member.model.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class MyBidItemListResponse {
    private List<MyBidItemResponse> items; // 상품 데이터
    private Pagination<MyBidItemResponse> pagination; // 페이지 관련 데이터

    @Builder
    public MyBidItemListResponse(List<MyBidItemResponse> items, Pagination<MyBidItemResponse> pagination) {
        this.items = items;
        this.pagination = pagination;
    }

    public static MyBidItemListResponse of(Page<Item> itemPage, Member viewer) {
        Page<MyBidItemResponse> myItemResponsePage = itemPage.map(item -> MyBidItemResponse.of(item, viewer));

        return MyBidItemListResponse.builder()
                .items(myItemResponsePage.getContent())
                .pagination(Pagination.of(myItemResponsePage))
                .build();
    }

    @Getter
    @NoArgsConstructor
    public static class MyBidItemResponse {

        private Long itemId; // 상품 접근
        private String title;
        private String description;
        private LocalDateTime expiredAt;
        private Integer hopePrice;
        private String itemStatus;
        private Integer bidCount;
        private Integer likeCount;
        private Integer viewCount;
        private String thumbnailImgUrl;
        @Builder
        private MyBidItemResponse(Long itemId, String title, String description, LocalDateTime expiredAt,Integer hopePrice, String itemStatus, Integer bidCount, Integer likeCount, Integer viewCount, String thumbnailImgUrl, Boolean likeStatus) {
            this.itemId = itemId;
            this.title = title;
            this.description = description;
            this.expiredAt = expiredAt;
            this.hopePrice = hopePrice;
            this.itemStatus = itemStatus;
            this.bidCount = bidCount;
            this.likeCount = likeCount;
            this.viewCount = viewCount;
            this.thumbnailImgUrl = thumbnailImgUrl;
        }

        private static MyBidItemResponse of(Item item, Member viewer) {

            return MyBidItemResponse.builder()
                    .itemId(item.getId())
                    .title(item.getTitle())
                    .description(item.getDescription())
                    .hopePrice(item.getHopePrice())
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

package com.api.farmingsoon.domain.item.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
public class ItemBySubQueryResponse {
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

    @QueryProjection
    public ItemBySubQueryResponse(Long itemId, String title, String description, LocalDateTime expiredAt, Integer highestPrice, Integer hopePrice, Integer lowestPrice, String itemStatus, Integer bidCount, Integer likeCount, Integer viewCount, String thumbnailImgUrl, Boolean likeStatus) {
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


}

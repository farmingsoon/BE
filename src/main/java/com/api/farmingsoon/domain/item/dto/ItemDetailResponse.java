package com.api.farmingsoon.domain.item.dto;

import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.domain.ItemStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
public class ItemDetailResponse {

    private String sellerProfileImgUrl;
    private String sellerNickname;
    private String title;
    private String description;
    private Integer highestPrice;
    private Integer hopePrice;
    private Integer lowestPrice;
    private LocalDateTime expiredAt;
    private String itemStatus;
    private Integer bidCount;
    private Integer likeCount;
    private Integer viewCount;


    public static ItemDetailResponse fromEntity(Item item) {
        return ItemDetailResponse.builder()
                .sellerProfileImgUrl(item.getMember().getProfileImg())
                .sellerNickname(item.getMember().getNickname())
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
                .build();
    }

}

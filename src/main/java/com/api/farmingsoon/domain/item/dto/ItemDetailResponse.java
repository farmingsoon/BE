package com.api.farmingsoon.domain.item.dto;

import com.api.farmingsoon.domain.bid.model.Bid;
import com.api.farmingsoon.domain.image.domain.Image;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.like.model.LikeableItem;
import com.api.farmingsoon.domain.member.model.Member;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
public class ItemDetailResponse {

    private Long sellerId;
    private String sellerProfileImgUrl;
    private String thumbnailImgUrl;
    private List<String> itemImgUrl;
    private String sellerNickname;
    private String title;
    private String description;
    private Integer hopePrice;
    private Integer awardPrice;
    private Integer highestPrice;
    private Integer lowestPrice;
    private LocalDateTime expiredAt;
    private String itemStatus;
    private Integer bidCount;
    private Integer likeCount;
    private Integer viewCount;
    private Boolean likeStatus;


    public static ItemDetailResponse of(Item item, Optional<Member> viewer) {

        return ItemDetailResponse.builder()
                .sellerId(item.getMember().getId())
                .sellerProfileImgUrl(item.getMember().getProfileImg())
                .sellerNickname(item.getMember().getNickname())
                .title(item.getTitle())
                .description(item.getDescription())
                .hopePrice(item.getHopePrice())
                .awardPrice(item.getAwardPrice())
                .highestPrice(item.getBidList().stream().map(Bid::getPrice).max(Integer::compareTo).orElse(null))
                .lowestPrice(item.getBidList().stream().map(Bid::getPrice).min(Integer::compareTo).orElse(null))
                .expiredAt(item.getExpiredAt())
                .itemStatus(item.getItemStatus().getStatus())
                .bidCount(item.getBidList().size())
                .viewCount(item.getViewCount())
                .likeCount(item.getLikeableItemList().size())
                .thumbnailImgUrl(item.getThumbnailImageUrl())
                .itemImgUrl(item.getImageList().stream().map(Image::getImageUrl).toList())
                .likeStatus // 조회자 세션이 존재할 경우에만 비교를 한다.
                    (
                        viewer.isPresent() ?
                        item.getLikeableItemList().stream().map(LikeableItem::getMember).toList().contains(viewer.get()) : false
                    )
                .build();
    }

}

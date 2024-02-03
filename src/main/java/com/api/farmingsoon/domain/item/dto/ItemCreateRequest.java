package com.api.farmingsoon.domain.item.dto;


import com.api.farmingsoon.common.util.TimeUtils;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.domain.ItemStatus;
import jakarta.persistence.Column;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCreateRequest {


    private String title;

    private String description;

    private Integer hopePrice;

    private Integer period;

    private MultipartFile thumbnailImage;

    private List<MultipartFile> images;

    @Builder
    private ItemCreateRequest(String title, String description, Integer hopePrice, Integer period, MultipartFile thumbnailImage, List<MultipartFile> images) {
        this.title = title;
        this.description = description;
        this.hopePrice = hopePrice;
        this.period = period;
        this.thumbnailImage = thumbnailImage;
        this.images = images;
    }

    public Item toEntity(){
        return Item.builder()
                .title(this.title)
                .description(this.description)
                .hopePrice(this.hopePrice)
                .expiredAt(TimeUtils.setExpireAt(period))
                .itemStatus(ItemStatus.BIDDING)
                .build();
    }
}

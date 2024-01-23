package com.api.farmingsoon.domain.item.dto;


import com.api.farmingsoon.common.util.TimeUtils;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.domain.ItemStatus;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCreateRequest {


    private String title;

    private String description;

    private Long hopePrice;

    private Integer period;

    private MultipartFile thumbnailImage;

    private List<MultipartFile> images;

    public Item toEntity(){
        return Item.builder()
                .title(this.title)
                .description(this.description)
                .hopePrice(this.hopePrice)
                .expiredAt(TimeUtils.setExpireAt(period))
                .itemStatus(ItemStatus.BIDDING)
                .deleted(false)
                .build();
    }
}
